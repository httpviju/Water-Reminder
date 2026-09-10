package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DayProgress
import com.example.data.WaterDatabase
import com.example.data.WaterLog
import com.example.data.WaterRepository
import com.example.util.WaterSoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class WaterUiState(
    val dailyTargetMl: Int = 3000,
    val todayConsumedMl: Int = 0,
    val remainingMl: Int = 3000,
    val completionPercentage: Int = 0,
    val streakDays: Int = 0,
    val nextReminderTime: String = "10:30 AM",
    val reminderEnabled: Boolean = true,
    val reminderIntervalMinutes: Int = 90,
    val countdownText: String = "",
    val isAlarmRinging: Boolean = false,
    val waterAlarmSoundEnabled: Boolean = true,
    val darkModeSetting: String = "dark", // Default to Spotify Dark mode
    val todayLogs: List<WaterLog> = emptyList(),
    val allLogs: List<WaterLog> = emptyList(),
    val weeklyProgress: List<DayProgress> = emptyList()
)

class WaterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WaterRepository
    private val prefs = application.getSharedPreferences("water_reminder_prefs", Context.MODE_PRIVATE)

    private val _dailyTargetMl = MutableStateFlow(prefs.getInt("daily_target", 3000))
    val dailyTargetMl: StateFlow<Int> = _dailyTargetMl.asStateFlow()

    private val _reminderInterval = MutableStateFlow(prefs.getInt("reminder_interval", 90).coerceIn(1, 720))
    val reminderInterval: StateFlow<Int> = _reminderInterval.asStateFlow()

    private val _reminderEnabled = MutableStateFlow(prefs.getBoolean("reminder_enabled", true))
    val reminderEnabled: StateFlow<Boolean> = _reminderEnabled.asStateFlow()

    private val _waterAlarmSoundEnabled = MutableStateFlow(prefs.getBoolean("alarm_sound_enabled", true))
    val waterAlarmSoundEnabled: StateFlow<Boolean> = _waterAlarmSoundEnabled.asStateFlow()

    private val _darkModeSetting = MutableStateFlow(prefs.getString("dark_mode", "dark") ?: "dark")
    val darkModeSetting: StateFlow<String> = _darkModeSetting.asStateFlow()

    private val _nextReminderTime = MutableStateFlow(calculateNextReminderTime(_reminderInterval.value))
    val nextReminderTime: StateFlow<String> = _nextReminderTime.asStateFlow()

    private val _countdownText = MutableStateFlow("")
    val countdownText: StateFlow<String> = _countdownText.asStateFlow()

    private val _isAlarmRinging = MutableStateFlow(false)
    val isAlarmRinging: StateFlow<Boolean> = _isAlarmRinging.asStateFlow()

    private var reminderTimerJob: Job? = null
    private var targetAlarmTimeMs: Long = 0L

    init {
        val db = WaterDatabase.getDatabase(application)
        repository = WaterRepository(db.waterDao())
        if (_reminderEnabled.value) {
            startReminderTimer()
        }
    }

    val todayLogs: StateFlow<List<WaterLog>> = repository.getTodayLogs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allLogs: StateFlow<List<WaterLog>> = repository.getAllLogs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private data class PrefsGroupA(
        val dailyTargetMl: Int,
        val reminderEnabled: Boolean,
        val reminderInterval: Int,
        val waterAlarmSound: Boolean
    )

    private data class PrefsGroupB(
        val darkModeSetting: String,
        val nextReminderTime: String,
        val countdownText: String,
        val isAlarmRinging: Boolean
    )

    private val groupAFlow = combine(
        _dailyTargetMl,
        _reminderEnabled,
        _reminderInterval,
        _waterAlarmSoundEnabled
    ) { target, remEnabled, remInterval, alarmSound ->
        PrefsGroupA(target, remEnabled, remInterval, alarmSound)
    }

    private val groupBFlow = combine(
        _darkModeSetting,
        _nextReminderTime,
        _countdownText,
        _isAlarmRinging
    ) { dark, nextRem, countdown, ringing ->
        PrefsGroupB(dark, nextRem, countdown, ringing)
    }

    val uiState: StateFlow<WaterUiState> = combine(
        todayLogs,
        allLogs,
        combine(groupAFlow, groupBFlow) { a, b -> Pair(a, b) }
    ) { todayList, allList, (groupA, groupB) ->
        val consumed = todayList.sumOf { it.amountMl }
        val remaining = (groupA.dailyTargetMl - consumed).coerceAtLeast(0)
        val percentage = if (groupA.dailyTargetMl > 0) ((consumed.toFloat() / groupA.dailyTargetMl) * 100).toInt() else 0
        val streak = repository.calculateStreak(allList, groupA.dailyTargetMl)
        val weekly = repository.getPast7DaysProgress(allList, groupA.dailyTargetMl)

        WaterUiState(
            dailyTargetMl = groupA.dailyTargetMl,
            todayConsumedMl = consumed,
            remainingMl = remaining,
            completionPercentage = percentage,
            streakDays = streak,
            nextReminderTime = groupB.nextReminderTime,
            reminderEnabled = groupA.reminderEnabled,
            reminderIntervalMinutes = groupA.reminderInterval,
            countdownText = groupB.countdownText,
            isAlarmRinging = groupB.isAlarmRinging,
            waterAlarmSoundEnabled = groupA.waterAlarmSound,
            darkModeSetting = groupB.darkModeSetting,
            todayLogs = todayList,
            allLogs = allList,
            weeklyProgress = weekly
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WaterUiState()
    )

    private fun startReminderTimer() {
        reminderTimerJob?.cancel()
        if (!_reminderEnabled.value) {
            _countdownText.value = ""
            return
        }

        val intervalMs = _reminderInterval.value * 60 * 1000L
        targetAlarmTimeMs = System.currentTimeMillis() + intervalMs
        _nextReminderTime.value = calculateNextReminderTime(_reminderInterval.value)

        reminderTimerJob = viewModelScope.launch {
            while (isActive && _reminderEnabled.value) {
                delay(1000L)
                val diff = targetAlarmTimeMs - System.currentTimeMillis()
                if (diff <= 0) {
                    _isAlarmRinging.value = true
                    if (_waterAlarmSoundEnabled.value) {
                        WaterSoundManager.playWaterAlarmSound()
                    }
                    targetAlarmTimeMs = System.currentTimeMillis() + intervalMs
                    _nextReminderTime.value = calculateNextReminderTime(_reminderInterval.value)
                } else {
                    val totalSecs = (diff / 1000).coerceAtLeast(0)
                    val mins = totalSecs / 60
                    val secs = totalSecs % 60
                    _countdownText.value = String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
                }
            }
        }
    }

    fun dismissAlarm() {
        _isAlarmRinging.value = false
    }

    fun addWater(amountMl: Int) {
        _isAlarmRinging.value = false
        if (_waterAlarmSoundEnabled.value) {
            WaterSoundManager.playWaterDropSound()
        }
        viewModelScope.launch {
            repository.logWater(amountMl)
            startReminderTimer()
        }
    }

    fun playWaterAlarmSound() {
        WaterSoundManager.playWaterAlarmSound()
    }

    fun playWaterDropSound() {
        WaterSoundManager.playWaterDropSound()
    }

    fun toggleWaterAlarmSound(enabled: Boolean) {
        _waterAlarmSoundEnabled.value = enabled
        prefs.edit().putBoolean("alarm_sound_enabled", enabled).apply()
        if (enabled) {
            WaterSoundManager.playWaterAlarmSound()
        }
    }

    fun removeWaterLog(id: Long) {
        viewModelScope.launch {
            repository.removeLog(id)
        }
    }

    fun setDailyTarget(targetMl: Int) {
        val validTarget = targetMl.coerceIn(500, 10000)
        _dailyTargetMl.value = validTarget
        prefs.edit().putInt("daily_target", validTarget).apply()
    }

    fun setReminderInterval(minutes: Int) {
        val validInterval = minutes.coerceIn(1, 720)
        _reminderInterval.value = validInterval
        prefs.edit().putInt("reminder_interval", validInterval).apply()
        _isAlarmRinging.value = false
        startReminderTimer()
    }

    fun toggleReminder(enabled: Boolean) {
        _reminderEnabled.value = enabled
        prefs.edit().putBoolean("reminder_enabled", enabled).apply()
        if (enabled) {
            startReminderTimer()
            if (_waterAlarmSoundEnabled.value) {
                WaterSoundManager.playWaterAlarmSound()
            }
        } else {
            reminderTimerJob?.cancel()
            _countdownText.value = ""
            _isAlarmRinging.value = false
        }
    }

    fun setDarkModeSetting(mode: String) {
        _darkModeSetting.value = mode
        prefs.edit().putString("dark_mode", mode).apply()
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAll()
        }
    }

    private fun calculateNextReminderTime(intervalMinutes: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, intervalMinutes)
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        return timeFormat.format(calendar.time)
    }
}
