package net.hovancik.stretchly

data class GoalMode(
    val id: String,
    val name: String,
    val description: String,
    val microbreakInterval: Int, // minutes
    val microbreakDuration: Int, // seconds
    val breakInterval: Int, // minutes
    val breakDuration: Int, // minutes
    val longBreakAfterMicrobreaks: Int = 0, // 0 = disabled
    val workHours: WorkHours = WorkHours(),
    val isCustom: Boolean = false
)

data class WorkHours(
    val startHour: Int = 9,
    val endHour: Int = 17,
    val weekdaysOnly: Boolean = true
)

object GoalModeManager {
    
    private val presetModes = listOf(
        GoalMode(
            id = "pomodoro",
            name = "Pomodoro",
            description = "25-minute work sessions with 5-minute breaks",
            microbreakInterval = 25,
            microbreakDuration = 0, // No microbreaks in Pomodoro
            breakInterval = 25,
            breakDuration = 5,
            longBreakAfterMicrobreaks = 4
        ),
        GoalMode(
            id = "balanced",
            name = "Balanced",
            description = "Regular microbreaks with periodic long breaks",
            microbreakInterval = 10,
            microbreakDuration = 20,
            breakInterval = 30,
            breakDuration = 5
        ),
        GoalMode(
            id = "gentle",
            name = "Gentle",
            description = "Fewer prompts with longer intervals",
            microbreakInterval = 20,
            microbreakDuration = 30,
            breakInterval = 60,
            breakDuration = 10
        ),
        GoalMode(
            id = "intensive",
            name = "Intensive",
            description = "Frequent short breaks for high-focus work",
            microbreakInterval = 5,
            microbreakDuration = 15,
            breakInterval = 15,
            breakDuration = 3
        )
    )
    
    private val customModes = mutableListOf<GoalMode>()
    
    fun getPresetModes(): List<GoalMode> = presetModes
    
    fun getCustomModes(): List<GoalMode> = customModes.toList()
    
    fun getAllModes(): List<GoalMode> = presetModes + customModes
    
    fun getCurrentMode(): GoalMode {
        return try {
            val settingsManager = SettingsManager(StretchlyApplication.instance)
            val currentModeId = settingsManager.getString("current_goal_mode", "balanced")
            getAllModes().find { it.id == currentModeId } ?: presetModes[1] // Default to balanced
        } catch (e: Exception) {
            // Fallback for testing or when application is not initialized
            presetModes[1] // Default to balanced
        }
    }
    
    fun setCurrentMode(modeId: String) {
        try {
            val settingsManager = SettingsManager(StretchlyApplication.instance)
            settingsManager.putString("current_goal_mode", modeId)
            
            val mode = getAllModes().find { it.id == modeId }
            mode?.let { applyMode(it) }
        } catch (e: Exception) {
            // Ignore for testing or when application is not initialized
        }
    }
    
    fun createCustomMode(
        name: String,
        description: String,
        microbreakInterval: Int,
        microbreakDuration: Int,
        breakInterval: Int,
        breakDuration: Int,
        longBreakAfterMicrobreaks: Int = 0,
        workHours: WorkHours = WorkHours()
    ): GoalMode {
        val customMode = GoalMode(
            id = "custom_${System.currentTimeMillis()}",
            name = name,
            description = description,
            microbreakInterval = microbreakInterval,
            microbreakDuration = microbreakDuration,
            breakInterval = breakInterval,
            breakDuration = breakDuration,
            longBreakAfterMicrobreaks = longBreakAfterMicrobreaks,
            workHours = workHours,
            isCustom = true
        )
        
        customModes.add(customMode)
        saveCustomModes()
        return customMode
    }
    
    fun deleteCustomMode(modeId: String) {
        customModes.removeAll { it.id == modeId }
        saveCustomModes()
    }
    
    fun updateCustomMode(
        modeId: String,
        name: String,
        description: String,
        microbreakInterval: Int,
        microbreakDuration: Int,
        breakInterval: Int,
        breakDuration: Int,
        longBreakAfterMicrobreaks: Int = 0,
        workHours: WorkHours = WorkHours()
    ): GoalMode? {
        val index = customModes.indexOfFirst { it.id == modeId }
        if (index != -1) {
            val updatedMode = GoalMode(
                id = modeId,
                name = name,
                description = description,
                microbreakInterval = microbreakInterval,
                microbreakDuration = microbreakDuration,
                breakInterval = breakInterval,
                breakDuration = breakDuration,
                longBreakAfterMicrobreaks = longBreakAfterMicrobreaks,
                workHours = workHours,
                isCustom = true
            )
            customModes[index] = updatedMode
            saveCustomModes()
            return updatedMode
        }
        return null
    }
    
    private fun applyMode(mode: GoalMode) {
        try {
            val settingsManager = SettingsManager(StretchlyApplication.instance)
            
            // Apply the mode settings
            settingsManager.putLong("microbreakInterval", mode.microbreakInterval * 60000L)
            settingsManager.putLong("microbreakDuration", mode.microbreakDuration * 1000L)
            settingsManager.putLong("breakInterval", mode.breakInterval * 60000L)
            settingsManager.putLong("breakDuration", mode.breakDuration * 60000L)
            
            // Enable/disable microbreaks based on duration
            settingsManager.putBoolean("microbreakEnabled", mode.microbreakDuration > 0)
            settingsManager.putBoolean("breakEnabled", mode.breakDuration > 0)
            
            // Save work hours
            settingsManager.putInt("work_start_hour", mode.workHours.startHour)
            settingsManager.putInt("work_end_hour", mode.workHours.endHour)
            settingsManager.putBoolean("weekdays_only", mode.workHours.weekdaysOnly)
            
            // Save long break settings
            settingsManager.putInt("long_break_after_microbreaks", mode.longBreakAfterMicrobreaks)
            
            // Trigger service reschedule
            val intent = android.content.Intent(StretchlyApplication.instance, BreakSchedulerService::class.java).apply {
                action = BreakSchedulerService.ACTION_RESCHEDULE
            }
            StretchlyApplication.instance.startService(intent)
        } catch (e: Exception) {
            // Ignore for testing or when application is not initialized
        }
    }
    
    private fun saveCustomModes() {
        try {
            val settingsManager = SettingsManager(StretchlyApplication.instance)
            // In a real app, you'd serialize to JSON and save
            // For now, we'll just save the count
            settingsManager.putInt("custom_modes_count", customModes.size)
        } catch (e: Exception) {
            // Ignore for testing or when application is not initialized
        }
    }
    
    private fun loadCustomModes() {
        try {
            // In a real app, you'd load from JSON using SettingsManager
            // For now, we'll just initialize with empty list
            // val settingsManager = SettingsManager(StretchlyApplication.instance)
        } catch (e: Exception) {
            // Ignore for testing or when application is not initialized
        }
    }
    
    fun isWithinWorkHours(): Boolean {
        try {
            val currentMode = getCurrentMode()
            val workHours = currentMode.workHours
            
            val calendar = java.util.Calendar.getInstance()
            val currentHour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
            val currentDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
            
            // Check if within work hours
            val withinHours = currentHour >= workHours.startHour && currentHour < workHours.endHour
            
            // Check if within work days
            val withinDays = if (workHours.weekdaysOnly) {
                currentDayOfWeek >= java.util.Calendar.MONDAY && currentDayOfWeek <= java.util.Calendar.FRIDAY
            } else {
                true
            }
            
            return withinHours && withinDays
        } catch (e: Exception) {
            // Default to true for testing or when application is not initialized
            return true
        }
    }
    
    fun getDailyTargets(): Map<String, Int> {
        try {
            val currentMode = getCurrentMode()
            val workHours = currentMode.workHours
            val workDayMinutes = (workHours.endHour - workHours.startHour) * 60
            
            val microbreakTarget = if (currentMode.microbreakDuration > 0) {
                workDayMinutes / currentMode.microbreakInterval
            } else {
                0
            }
            
            val breakTarget = if (currentMode.breakDuration > 0) {
                workDayMinutes / currentMode.breakInterval
            } else {
                0
            }
            
            return mapOf(
                "microbreaks" to microbreakTarget,
                "breaks" to breakTarget
            )
        } catch (e: Exception) {
            // Default values for testing or when application is not initialized
            return mapOf(
                "microbreaks" to 8,
                "breaks" to 2
            )
        }
    }
}
