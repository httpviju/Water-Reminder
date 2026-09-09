package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DayProgress
import com.example.data.WaterDatabase
import com.example.data.WaterLog
import com.example.data.WaterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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
    val darkModeSetting: String = "system", // "system", "light", "dark"
    val todayLogs: List<WaterLog> = emptyList(),
    val allLogs: List<WaterLog> = emptyList(),
    val weeklyProgress: List<DayProgress> = emptyList()
)

class WaterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WaterRepository
    private val prefs = application.getSharedPreferences("water_reminder_prefs", Context.MODE_PRIVATE)

    private val _dailyTargetMl = MutableStateFlow(prefs.getInt("daily_target", 3000))
    val dailyTargetMl: StateFlow<Int> = _dailyTargetMl.asStateFlow()

    private val _reminderInterval = MutableStateFlow(prefs.getInt("reminder_interval", 90))
    val reminderInterval: StateFlow<Int> = _reminderInterval.asStateFlow()

    private val _reminderEnabled = MutableStateFlow(prefs.getBoolean("reminder_enabled", true))
    val reminderEnabled: StateFlow<Boolean> = _reminderEnabled.asStateFlow()

    private val _darkModeSetting = MutableStateFlow(prefs.getString("dark_mode", "system") ?: "system")
    val darkModeSetting: StateFlow<String> = _darkModeSetting.asStateFlow()

    private val _nextReminderTime = MutableStateFlow(calculateNextReminderTime(_reminderInterval.value))
    val nextReminderTime: StateFlow<String> = _nextReminderTime.asStateFlow()

    init {
        val db = WaterDatabase.getDatabase(application)
        repository = WaterRepository(db.waterDao())
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

    private data class UserPreferences(
        val dailyTargetMl: Int,
        val reminderEnabled: Boolean,
        val reminderInterval: Int,
        val darkModeSetting: String,
        val nextReminderTime: String
    )

    private val preferencesFlow = combine(
        _dailyTargetMl,
        _reminderEnabled,
        _reminderInterval,
        _darkModeSetting,
        _nextReminderTime
    ) { target, remEnabled, remInterval, darkSetting, nextRem ->
        UserPreferences(target, remEnabled, remInterval, darkSetting, nextRem)
    }

    val uiState: StateFlow<WaterUiState> = combine(
        todayLogs,
        allLogs,
        preferencesFlow
    ) { todayList, allList, prefs ->
        val consumed = todayList.sumOf { it.amountMl }
        val remaining = (prefs.dailyTargetMl - consumed).coerceAtLeast(0)
        val percentage = if (prefs.dailyTargetMl > 0) ((consumed.toFloat() / prefs.dailyTargetMl) * 100).toInt() else 0
        val streak = repository.calculateStreak(allList, prefs.dailyTargetMl)
        val weekly = repository.getPast7DaysProgress(allList, prefs.dailyTargetMl)

        WaterUiState(
            dailyTargetMl = prefs.dailyTargetMl,
            todayConsumedMl = consumed,
            remainingMl = remaining,
            completionPercentage = percentage,
            streakDays = streak,
            nextReminderTime = prefs.nextReminderTime,
            reminderEnabled = prefs.reminderEnabled,
            reminderIntervalMinutes = prefs.reminderInterval,
            darkModeSetting = prefs.darkModeSetting,
            todayLogs = todayList,
            allLogs = allList,
            weeklyProgress = weekly
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WaterUiState()
    )

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            repository.logWater(amountMl)
            // recalculate next reminder
            _nextReminderTime.value = calculateNextReminderTime(_reminderInterval.value)
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
        val validInterval = minutes.coerceIn(15, 240)
        _reminderInterval.value = validInterval
        prefs.edit().putInt("reminder_interval", validInterval).apply()
        _nextReminderTime.value = calculateNextReminderTime(validInterval)
    }

    fun toggleReminder(enabled: Boolean) {
        _reminderEnabled.value = enabled
        prefs.edit().putBoolean("reminder_enabled", enabled).apply()
        if (enabled) {
            _nextReminderTime.value = calculateNextReminderTime(_reminderInterval.value)
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
