package net.hovancik.stretchly

import java.util.*

data class BreakRecord(
    val type: String, // "microbreak" or "break"
    val timestamp: Long,
    val duration: Long,
    val wasSkipped: Boolean = false,
    val wasSnoozed: Boolean = false,
    val reason: String? = null // "dnd", "manual", "meeting", etc.
)

data class DailyStats(
    val date: Date,
    val microbreaksTaken: Int,
    val microbreaksSkipped: Int,
    val breaksTaken: Int,
    val breaksSkipped: Int,
    val totalTimeProtected: Long, // in minutes
    val averageScreenTime: Long, // in minutes
    val snoozeCount: Int
)

data class WeeklyStats(
    val weekStart: Date,
    val totalMicrobreaks: Int,
    val totalBreaks: Int,
    val adherenceRate: Double, // percentage
    val averageDailyTimeProtected: Long,
    val mostActiveDay: String
)

object InsightsManager {
    
    private val settingsManager = SettingsManager(StretchlyApplication.instance)
    private val breakRecords = mutableListOf<BreakRecord>()
    
    fun recordBreak(breakType: String, duration: Long, wasSkipped: Boolean = false, wasSnoozed: Boolean = false, reason: String? = null) {
        val record = BreakRecord(
            type = breakType,
            timestamp = System.currentTimeMillis(),
            duration = duration,
            wasSkipped = wasSkipped,
            wasSnoozed = wasSnoozed,
            reason = reason
        )
        
        breakRecords.add(record)
        saveBreakRecords()
        
        // Also record for streaks
        if (!wasSkipped) {
            StreakManager.recordBreak(breakType)
        }
    }
    
    fun recordGuidedRoutine(routineType: String) {
        StreakManager.recordGuidedRoutine(routineType)
        
        if (routineType == "eye_exercises") {
            StreakManager.recordEyeCareRoutine()
        }
    }
    
    fun getDailyStats(date: Date): DailyStats {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val dayStart = calendar.time.time
        
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val dayEnd = calendar.time.time
        
        val dayRecords = breakRecords.filter { 
            it.timestamp >= dayStart && it.timestamp < dayEnd 
        }
        
        val microbreaksTaken = dayRecords.count { it.type == "microbreak" && !it.wasSkipped }
        val microbreaksSkipped = dayRecords.count { it.type == "microbreak" && it.wasSkipped }
        val breaksTaken = dayRecords.count { it.type == "break" && !it.wasSkipped }
        val breaksSkipped = dayRecords.count { it.type == "break" && it.wasSkipped }
        
        val totalTimeProtected = dayRecords
            .filter { !it.wasSkipped }
            .sumOf { it.duration / 60000 } // Convert to minutes
        
        val snoozeCount = dayRecords.count { it.wasSnoozed }
        
        // Calculate average screen time (simplified - assumes 8 hour workday)
        val workDayMinutes = 8L * 60L
        val totalBreaks = microbreaksTaken + breaksTaken
        val averageScreenTime = if (totalBreaks > 0) workDayMinutes / totalBreaks else workDayMinutes
        
        return DailyStats(
            date = date,
            microbreaksTaken = microbreaksTaken,
            microbreaksSkipped = microbreaksSkipped,
            breaksTaken = breaksTaken,
            breaksSkipped = breaksSkipped,
            totalTimeProtected = totalTimeProtected,
            averageScreenTime = averageScreenTime,
            snoozeCount = snoozeCount
        )
    }
    
    fun getWeeklyStats(weekStart: Date): WeeklyStats {
        val calendar = Calendar.getInstance()
        calendar.time = weekStart
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val weekStartTime = calendar.time.time
        
        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        val weekEndTime = calendar.time.time
        
        val weekRecords = breakRecords.filter { 
            it.timestamp >= weekStartTime && it.timestamp < weekEndTime 
        }
        
        val totalMicrobreaks = weekRecords.count { it.type == "microbreak" && !it.wasSkipped }
        val totalBreaks = weekRecords.count { it.type == "break" && !it.wasSkipped }
        val totalSkipped = weekRecords.count { it.wasSkipped }
        val totalScheduled = weekRecords.size
        
        val adherenceRate = if (totalScheduled > 0) {
            ((totalScheduled - totalSkipped).toDouble() / totalScheduled) * 100
        } else {
            0.0
        }
        
        val totalTimeProtected = weekRecords
            .filter { !it.wasSkipped }
            .sumOf { it.duration / 60000 }
        
        val averageDailyTimeProtected = totalTimeProtected / 7
        
        // Find most active day
        val dailyCounts = mutableMapOf<String, Int>()
        val dayNames = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        
        weekRecords.forEach { record ->
            val recordDate = Date(record.timestamp)
            val dayOfWeek = Calendar.getInstance().apply { time = recordDate }.get(Calendar.DAY_OF_WEEK)
            val dayName = dayNames[dayOfWeek - 1]
            dailyCounts[dayName] = (dailyCounts[dayName] ?: 0) + 1
        }
        
        val mostActiveDay = dailyCounts.maxByOrNull { it.value }?.key ?: "Unknown"
        
        return WeeklyStats(
            weekStart = weekStart,
            totalMicrobreaks = totalMicrobreaks,
            totalBreaks = totalBreaks,
            adherenceRate = adherenceRate,
            averageDailyTimeProtected = averageDailyTimeProtected,
            mostActiveDay = mostActiveDay
        )
    }
    
    fun getMonthlyStats(monthStart: Date): Map<String, Any> {
        val calendar = Calendar.getInstance()
        calendar.time = monthStart
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val monthStartTime = calendar.time.time
        
        calendar.add(Calendar.MONTH, 1)
        val monthEndTime = calendar.time.time
        
        val monthRecords = breakRecords.filter { 
            it.timestamp >= monthStartTime && it.timestamp < monthEndTime 
        }
        
        val totalBreaks = monthRecords.count { !it.wasSkipped }
        val totalSkipped = monthRecords.count { it.wasSkipped }
        val totalScheduled = monthRecords.size
        
        val adherenceRate = if (totalScheduled > 0) {
            ((totalScheduled - totalSkipped).toDouble() / totalScheduled) * 100
        } else {
            0.0
        }
        
        val totalTimeProtected = monthRecords
            .filter { !it.wasSkipped }
            .sumOf { it.duration / 60000 }
        
        val averageDailyTimeProtected = totalTimeProtected / 30
        
        val skipReasons = monthRecords
            .filter { it.wasSkipped }
            .groupBy { it.reason ?: "unknown" }
            .mapValues { it.value.size }
        
        return mapOf(
            "totalBreaks" to totalBreaks,
            "adherenceRate" to adherenceRate,
            "totalTimeProtected" to totalTimeProtected,
            "averageDailyTimeProtected" to averageDailyTimeProtected,
            "skipReasons" to skipReasons
        )
    }
    
    fun getBreakTrends(days: Int): List<DailyStats> {
        val calendar = Calendar.getInstance()
        val trends = mutableListOf<DailyStats>()
        
        for (i in 0 until days) {
            calendar.add(Calendar.DAY_OF_MONTH, -i)
            val date = calendar.time
            trends.add(getDailyStats(date))
            calendar.add(Calendar.DAY_OF_MONTH, i) // Reset
        }
        
        return trends.reversed()
    }
    
    fun exportData(): String {
        val csv = StringBuilder()
        csv.append("Date,Type,Timestamp,Duration,WasSkipped,WasSnoozed,Reason\n")
        
        breakRecords.forEach { record ->
            csv.append("${Date(record.timestamp)},${record.type},${record.timestamp},${record.duration},${record.wasSkipped},${record.wasSnoozed},${record.reason ?: ""}\n")
        }
        
        return csv.toString()
    }
    
    private fun saveBreakRecords() {
        // In a real app, you'd save to a database
        // For now, we'll keep in memory and save a summary to preferences
        val totalBreaks = breakRecords.size
        settingsManager.putInt("total_breaks_recorded", totalBreaks)
    }
    
    private fun loadBreakRecords() {
        // In a real app, you'd load from a database
        // For now, we'll just initialize with empty list
        // The records will be lost on app restart, but that's okay for this demo
    }
    
    fun clearAllData() {
        breakRecords.clear()
        settingsManager.putInt("total_breaks_recorded", 0)
        StreakManager.resetAllData()
    }
}
