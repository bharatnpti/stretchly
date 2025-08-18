package net.hovancik.stretchly

import java.util.*

data class Streak(
    val type: String, // "microbreak" or "break"
    val currentStreak: Int,
    val longestStreak: Int,
    val lastBreakDate: Date
)

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean = false,
    val unlockedDate: Date? = null
)

object StreakManager {
    
    private val settingsManager = SettingsManager(StretchlyApplication.instance)
    private val badges = mutableListOf<Badge>()
    
    init {
        initializeBadges()
    }
    
    private fun initializeBadges() {
        badges.addAll(listOf(
            Badge("first_break", "First Break", "Complete your first break", "🌟"),
            Badge("week_streak", "Week Warrior", "Complete breaks for 7 consecutive days", "🔥"),
            Badge("month_streak", "Monthly Master", "Complete breaks for 30 consecutive days", "💎"),
            Badge("eye_care_week", "Eye Care Week", "Use eye care routines for 7 days", "👁️"),
            Badge("breathing_master", "Breathing Master", "Complete 50 breathing exercises", "🫁"),
            Badge("variety_seeker", "Variety Seeker", "Try all guided routine types", "🎯"),
            Badge("consistency_king", "Consistency King", "Maintain 90% adherence for a month", "👑"),
            Badge("early_bird", "Early Bird", "Complete your first break before 9 AM", "🌅")
        ))
    }
    
    fun recordBreak(breakType: String) {
        val today = Date()
        val calendar = Calendar.getInstance()
        calendar.time = today
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val todayStart = calendar.time
        
        val streakKey = "${breakType}_streak"
        val lastBreakKey = "${breakType}_last_break"
        val longestStreakKey = "${breakType}_longest_streak"
        
        val lastBreakDate = settingsManager.getLong(lastBreakKey, 0L)
        val currentStreak = settingsManager.getInt(streakKey, 0)
        val longestStreak = settingsManager.getInt(longestStreakKey, 0)
        
        if (lastBreakDate == 0L) {
            // First break ever
            settingsManager.putInt(streakKey, 1)
            settingsManager.putLong(lastBreakKey, todayStart.time)
            settingsManager.putInt(longestStreakKey, 1)
            unlockBadge("first_break")
        } else {
            val lastBreak = Date(lastBreakDate)
            val calendar2 = Calendar.getInstance()
            calendar2.time = lastBreak
            calendar2.set(Calendar.HOUR_OF_DAY, 0)
            calendar2.set(Calendar.MINUTE, 0)
            calendar2.set(Calendar.SECOND, 0)
            calendar2.set(Calendar.MILLISECOND, 0)
            val lastBreakStart = calendar2.time
            
            val daysDifference = ((todayStart.time - lastBreakStart.time) / (1000 * 60 * 60 * 24)).toInt()
            
            if (daysDifference == 1) {
                // Consecutive day
                val newStreak = currentStreak + 1
                settingsManager.putInt(streakKey, newStreak)
                settingsManager.putLong(lastBreakKey, todayStart.time)
                
                if (newStreak > longestStreak) {
                    settingsManager.putInt(longestStreakKey, newStreak)
                }
                
                // Check for streak badges
                if (newStreak == 7) {
                    unlockBadge("week_streak")
                }
                if (newStreak == 30) {
                    unlockBadge("month_streak")
                }
            } else if (daysDifference == 0) {
                // Same day, don't update streak
            } else {
                // Streak broken
                settingsManager.putInt(streakKey, 1)
                settingsManager.putLong(lastBreakKey, todayStart.time)
            }
        }
        
        // Check for early bird badge
        if (calendar.get(Calendar.HOUR_OF_DAY) < 9) {
            unlockBadge("early_bird")
        }
    }
    
    fun getStreak(breakType: String): Streak {
        val streakKey = "${breakType}_streak"
        val lastBreakKey = "${breakType}_last_break"
        val longestStreakKey = "${breakType}_longest_streak"
        
        val currentStreak = settingsManager.getInt(streakKey, 0)
        val longestStreak = settingsManager.getInt(longestStreakKey, 0)
        val lastBreakDate = Date(settingsManager.getLong(lastBreakKey, 0L))
        
        return Streak(breakType, currentStreak, longestStreak, lastBreakDate)
    }
    
    fun recordGuidedRoutine(routineType: String) {
        val routineCountKey = "${routineType}_count"
        val currentCount = settingsManager.getInt(routineCountKey, 0)
        settingsManager.putInt(routineCountKey, currentCount + 1)
        
        // Check for breathing master badge
        if (routineType == "breathing" && currentCount + 1 >= 50) {
            unlockBadge("breathing_master")
        }
        
        // Check for variety seeker badge
        val breathingCount = settingsManager.getInt("breathing_count", 0)
        val eyeCount = settingsManager.getInt("eye_exercises_count", 0)
        val neckCount = settingsManager.getInt("neck_stretches_count", 0)
        
        if (breathingCount > 0 && eyeCount > 0 && neckCount > 0) {
            unlockBadge("variety_seeker")
        }
    }
    
    fun recordEyeCareRoutine() {
        val eyeCareDaysKey = "eye_care_days"
        val today = Date()
        val calendar = Calendar.getInstance()
        calendar.time = today
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val todayStart = calendar.time
        
        val lastEyeCareDate = settingsManager.getLong("last_eye_care_date", 0L)
        val eyeCareDays = settingsManager.getInt(eyeCareDaysKey, 0)
        
        if (lastEyeCareDate == 0L) {
            settingsManager.putInt(eyeCareDaysKey, 1)
            settingsManager.putLong("last_eye_care_date", todayStart.time)
        } else {
            val lastDate = Date(lastEyeCareDate)
            val calendar2 = Calendar.getInstance()
            calendar2.time = lastDate
            calendar2.set(Calendar.HOUR_OF_DAY, 0)
            calendar2.set(Calendar.MINUTE, 0)
            calendar2.set(Calendar.SECOND, 0)
            calendar2.set(Calendar.MILLISECOND, 0)
            val lastDateStart = calendar2.time
            
            val daysDifference = ((todayStart.time - lastDateStart.time) / (1000 * 60 * 60 * 24)).toInt()
            
            if (daysDifference == 1) {
                val newDays = eyeCareDays + 1
                settingsManager.putInt(eyeCareDaysKey, newDays)
                settingsManager.putLong("last_eye_care_date", todayStart.time)
                
                if (newDays == 7) {
                    unlockBadge("eye_care_week")
                }
            } else if (daysDifference == 0) {
                // Same day, don't update
            } else {
                // Reset streak
                settingsManager.putInt(eyeCareDaysKey, 1)
                settingsManager.putLong("last_eye_care_date", todayStart.time)
            }
        }
    }
    
    private fun unlockBadge(badgeId: String) {
        val badge = badges.find { it.id == badgeId }
        badge?.let {
            if (!it.isUnlocked) {
                val index = badges.indexOf(it)
                badges[index] = it.copy(isUnlocked = true, unlockedDate = Date())
                
                // Save to preferences
                settingsManager.putBoolean("badge_${badgeId}", true)
                settingsManager.putLong("badge_${badgeId}_date", Date().time)
            }
        }
    }
    
    fun getBadges(): List<Badge> {
        // Load unlocked status from preferences
        badges.forEach { badge ->
            val isUnlocked = settingsManager.getBoolean("badge_${badge.id}", false)
            val unlockedDate = settingsManager.getLong("badge_${badge.id}_date", 0L)
            
            if (isUnlocked && !badge.isUnlocked) {
                val index = badges.indexOf(badge)
                badges[index] = badge.copy(
                    isUnlocked = true,
                    unlockedDate = if (unlockedDate > 0) Date(unlockedDate) else null
                )
            }
        }
        
        return badges.toList()
    }
    
    fun getUnlockedBadges(): List<Badge> = badges.filter { it.isUnlocked }
    
    fun resetAllData() {
        // Reset all streak and badge data
        val keys = listOf(
            "microbreak_streak", "break_streak",
            "microbreak_last_break", "break_last_break",
            "microbreak_longest_streak", "break_longest_streak",
            "breathing_count", "eye_exercises_count", "neck_stretches_count",
            "eye_care_days", "last_eye_care_date"
        )
        
        keys.forEach { key ->
            settingsManager.remove(key)
        }
        
        // Reset badges
        badges.forEach { badge ->
            settingsManager.remove("badge_${badge.id}")
            settingsManager.remove("badge_${badge.id}_date")
        }
        
        // Reset badge objects
        badges.forEach { badge ->
            val index = badges.indexOf(badge)
            badges[index] = badge.copy(isUnlocked = false, unlockedDate = null)
        }
    }
}
