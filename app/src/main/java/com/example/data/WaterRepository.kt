package com.example.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DayProgress(
    val dayLabel: String,
    val dateString: String,
    val amountMl: Int,
    val targetMl: Int,
    val isToday: Boolean
) {
    val completionRatio: Float
        get() = if (targetMl > 0) (amountMl.toFloat() / targetMl).coerceIn(0f, 1f) else 0f
}

class WaterRepository(private val waterDao: WaterDao) {

    fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun getTodayLogs(): Flow<List<WaterLog>> {
        return waterDao.getLogsForDate(getTodayDateString())
    }

    fun getAllLogs(): Flow<List<WaterLog>> {
        return waterDao.getAllLogs()
    }

    suspend fun logWater(amountMl: Int) {
        val now = System.currentTimeMillis()
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(now))
        val entry = WaterLog(
            amountMl = amountMl,
            timestamp = now,
            dateString = dateString
        )
        waterDao.insertLog(entry)
    }

    suspend fun removeLog(id: Long) {
        waterDao.deleteLogById(id)
    }

    suspend fun resetAll() {
        waterDao.clearAll()
    }

    /**
     * Calculates consecutive days streak where target was reached.
     */
    fun calculateStreak(logs: List<WaterLog>, targetMl: Int): Int {
        if (logs.isEmpty() || targetMl <= 0) return 0

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val groupedByDate = logs.groupBy { it.dateString }

        val calendar = Calendar.getInstance()
        val todayStr = dateFormat.format(calendar.time)

        val todayConsumed = groupedByDate[todayStr]?.sumOf { it.amountMl } ?: 0
        var streak = 0

        if (todayConsumed >= targetMl) {
            streak++
        }

        // Check past consecutive days starting yesterday
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        while (true) {
            val dateStr = dateFormat.format(calendar.time)
            val consumed = groupedByDate[dateStr]?.sumOf { it.amountMl } ?: 0
            if (consumed >= targetMl) {
                streak++
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }

        return streak
    }

    /**
     * Returns the 7 days of the current week (e.g. past 7 days up to today).
     */
    fun getPast7DaysProgress(logs: List<WaterLog>, targetMl: Int): List<DayProgress> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayNameFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val groupedByDate = logs.groupBy { it.dateString }
        val calendar = Calendar.getInstance()
        val todayStr = dateFormat.format(calendar.time)

        val result = mutableListOf<DayProgress>()

        // 6 days ago up to today (7 days total)
        calendar.add(Calendar.DAY_OF_YEAR, -6)
        for (i in 0 until 7) {
            val dateStr = dateFormat.format(calendar.time)
            val dayLabel = dayNameFormat.format(calendar.time)
            val amount = groupedByDate[dateStr]?.sumOf { it.amountMl } ?: 0
            result.add(
                DayProgress(
                    dayLabel = dayLabel,
                    dateString = dateStr,
                    amountMl = amount,
                    targetMl = targetMl,
                    isToday = dateStr == todayStr
                )
            )
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return result
    }
}
