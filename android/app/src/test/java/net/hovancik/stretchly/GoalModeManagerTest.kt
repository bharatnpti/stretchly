package net.hovancik.stretchly

import org.junit.Test
import org.junit.Assert.*

class GoalModeManagerTest {

    @Test
    fun testCreateCustomMode() {
        val customMode = GoalModeManager.createCustomMode(
            name = "Test Mode",
            description = "A test custom mode",
            microbreakInterval = 15,
            microbreakDuration = 30,
            breakInterval = 45,
            breakDuration = 10,
            longBreakAfterMicrobreaks = 3,
            workHours = WorkHours(9, 17, true)
        )

        assertEquals("Test Mode", customMode.name)
        assertEquals("A test custom mode", customMode.description)
        assertEquals(15, customMode.microbreakInterval)
        assertEquals(30, customMode.microbreakDuration)
        assertEquals(45, customMode.breakInterval)
        assertEquals(10, customMode.breakDuration)
        assertEquals(3, customMode.longBreakAfterMicrobreaks)
        assertEquals(9, customMode.workHours.startHour)
        assertEquals(17, customMode.workHours.endHour)
        assertTrue(customMode.workHours.weekdaysOnly)
        assertTrue(customMode.isCustom)
    }

    @Test
    fun testUpdateCustomMode() {
        // Create a custom mode first
        val originalMode = GoalModeManager.createCustomMode(
            name = "Original Mode",
            description = "Original description",
            microbreakInterval = 10,
            microbreakDuration = 20,
            breakInterval = 30,
            breakDuration = 5
        )

        // Update the mode
        val updatedMode = GoalModeManager.updateCustomMode(
            modeId = originalMode.id,
            name = "Updated Mode",
            description = "Updated description",
            microbreakInterval = 20,
            microbreakDuration = 40,
            breakInterval = 60,
            breakDuration = 10,
            longBreakAfterMicrobreaks = 2,
            workHours = WorkHours(8, 18, false)
        )

        assertNotNull(updatedMode)
        assertEquals("Updated Mode", updatedMode!!.name)
        assertEquals("Updated description", updatedMode.description)
        assertEquals(20, updatedMode.microbreakInterval)
        assertEquals(40, updatedMode.microbreakDuration)
        assertEquals(60, updatedMode.breakInterval)
        assertEquals(10, updatedMode.breakDuration)
        assertEquals(2, updatedMode.longBreakAfterMicrobreaks)
        assertEquals(8, updatedMode.workHours.startHour)
        assertEquals(18, updatedMode.workHours.endHour)
        assertFalse(updatedMode.workHours.weekdaysOnly)
    }

    @Test
    fun testDeleteCustomMode() {
        // Create a custom mode
        val customMode = GoalModeManager.createCustomMode(
            name = "To Delete",
            description = "This will be deleted",
            microbreakInterval = 10,
            microbreakDuration = 20,
            breakInterval = 30,
            breakDuration = 5
        )

        // Verify it exists
        val customModes = GoalModeManager.getCustomModes()
        assertTrue(customModes.any { it.id == customMode.id })

        // Delete it
        GoalModeManager.deleteCustomMode(customMode.id)

        // Verify it's gone
        val customModesAfterDelete = GoalModeManager.getCustomModes()
        assertFalse(customModesAfterDelete.any { it.id == customMode.id })
    }

    @Test
    fun testGetPresetModes() {
        val presetModes = GoalModeManager.getPresetModes()
        
        // Should have at least 4 preset modes
        assertTrue(presetModes.size >= 4)
        
        // Check for specific preset modes
        val pomodoroMode = presetModes.find { it.id == "pomodoro" }
        assertNotNull(pomodoroMode)
        assertEquals("Pomodoro", pomodoroMode!!.name)
        
        val balancedMode = presetModes.find { it.id == "balanced" }
        assertNotNull(balancedMode)
        assertEquals("Balanced", balancedMode!!.name)
        
        val gentleMode = presetModes.find { it.id == "gentle" }
        assertNotNull(gentleMode)
        assertEquals("Gentle", gentleMode!!.name)
        
        val intensiveMode = presetModes.find { it.id == "intensive" }
        assertNotNull(intensiveMode)
        assertEquals("Intensive", intensiveMode!!.name)
    }

    @Test
    fun testWorkHoursValidation() {
        // Test valid work hours
        val validWorkHours = WorkHours(9, 17, true)
        assertEquals(9, validWorkHours.startHour)
        assertEquals(17, validWorkHours.endHour)
        assertTrue(validWorkHours.weekdaysOnly)

        // Test weekend work hours
        val weekendWorkHours = WorkHours(8, 20, false)
        assertEquals(8, weekendWorkHours.startHour)
        assertEquals(20, weekendWorkHours.endHour)
        assertFalse(weekendWorkHours.weekdaysOnly)
    }
}
