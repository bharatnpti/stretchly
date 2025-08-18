package net.hovancik.stretchly

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.*
import java.util.concurrent.TimeUnit

class BreakPlanner(private val service: BreakSchedulerService) {

    private val handler = Handler(Looper.getMainLooper())
    private var microbreakTimer: Timer? = null
    private var breakTimer: Timer? = null
    private val settingsManager = SettingsManager(service)
    private val dndManager = DndManager(service)
    private var nextBreakTime: Long = 0
    private var snoozedUntilTimeMillis: Long = 0
    private var skipNextMicrobreak: Boolean = false
    private var skipNextBreak: Boolean = false

    fun scheduleBreaks() {
        cancelTimers()
        if (settingsManager.getBoolean("microbreakEnabled", DefaultSettings.MICROBREAK_ENABLED)) {
            scheduleMicrobreak()
        }
        if (settingsManager.getBoolean("breakEnabled", DefaultSettings.BREAK_ENABLED)) {
            scheduleBreak()
        }
        updateNextBreakTime()
    }

    private fun scheduleMicrobreak() {
        microbreakTimer = Timer()
        val interval = settingsManager.getLong("microbreakInterval", DefaultSettings.MICROBREAK_INTERVAL.toLong())
        microbreakTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                handler.post {
                    if (shouldSuppressBreak()) {
                        Log.d("BreakPlanner", "Microbreak suppressed (paused/DND/snoozed)")
                        updateNextBreakTime()
                        return@post
                    }
                    if (skipNextMicrobreak) {
                        Log.d("BreakPlanner", "Microbreak skipped once")
                        skipNextMicrobreak = false
                        updateNextBreakTime()
                        return@post
                    }
                    Log.d("BreakPlanner", "Time for a microbreak!")
                    service.showBreakNotification("Time for a microbreak!", "microbreak")
                    InsightsManager.recordBreak("microbreak", 0, wasSkipped = false, wasSnoozed = false, reason = null)
                    updateNextBreakTime()
                }
            }
        }, interval, interval)
    }

    private fun scheduleBreak() {
        breakTimer = Timer()
        val interval = settingsManager.getLong("breakInterval", DefaultSettings.BREAK_INTERVAL.toLong())
        breakTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                handler.post {
                    if (shouldSuppressBreak()) {
                        Log.d("BreakPlanner", "Break suppressed (paused/DND/snoozed)")
                        updateNextBreakTime()
                        return@post
                    }
                    if (skipNextBreak) {
                        Log.d("BreakPlanner", "Break skipped once")
                        skipNextBreak = false
                        updateNextBreakTime()
                        return@post
                    }
                    Log.d("BreakPlanner", "Time for a break!")
                    service.showBreakNotification("Time for a break!", "break")
                    InsightsManager.recordBreak("break", 0, wasSkipped = false, wasSnoozed = false, reason = null)
                    updateNextBreakTime()
                }
            }
        }, interval, interval)
    }

    private fun updateNextBreakTime() {
        val microbreakInterval = settingsManager.getLong("microbreakInterval", DefaultSettings.MICROBREAK_INTERVAL.toLong())
        val breakInterval = settingsManager.getLong("breakInterval", DefaultSettings.BREAK_INTERVAL.toLong())
        val baseline = System.currentTimeMillis()
        val computedNextMicrobreakTime = baseline + microbreakInterval
        val computedNextBreakTime = baseline + breakInterval
        val candidateNextTime = if (settingsManager.getBoolean("microbreakEnabled", DefaultSettings.MICROBREAK_ENABLED) &&
            (!settingsManager.getBoolean("breakEnabled", DefaultSettings.BREAK_ENABLED) || computedNextMicrobreakTime < computedNextBreakTime)) {
            computedNextMicrobreakTime
        } else {
            computedNextBreakTime
        }

        this.nextBreakTime = maxOf(candidateNextTime, snoozedUntilTimeMillis)
        updateNotification()
    }

    private fun updateNotification() {
        val now = System.currentTimeMillis()
        if (isPaused()) {
            service.updateNotification("Breaks paused")
            return
        }
        if (dndManager.isDndEnabled()) {
            service.updateNotification("Breaks paused by Do Not Disturb")
            return
        }
        val timeRemaining = nextBreakTime - now
        val minutes = TimeUnit.MILLISECONDS.toMinutes(maxOf(0, timeRemaining))
        service.updateNotification("Next break in $minutes minutes")
    }

    fun stop() {
        microbreakTimer?.cancel()
        breakTimer?.cancel()
    }

    fun reschedule() {
        scheduleBreaks()
    }

    fun setPaused(paused: Boolean) {
        settingsManager.putBoolean("paused", paused)
        updateNextBreakTime()
    }

    fun isPaused(): Boolean {
        return settingsManager.getBoolean("paused", DefaultSettings.PAUSED)
    }

    fun snooze(minutes: Int) {
        val now = System.currentTimeMillis()
        snoozedUntilTimeMillis = maxOf(snoozedUntilTimeMillis, now + TimeUnit.MINUTES.toMillis(minutes.toLong()))
        updateNextBreakTime()
    }

    fun skipOnce(breakType: String) {
        if (breakType == "microbreak") {
            skipNextMicrobreak = true
        } else {
            skipNextBreak = true
        }
        updateNextBreakTime()
    }

    private fun cancelTimers() {
        microbreakTimer?.cancel()
        breakTimer?.cancel()
        microbreakTimer = null
        breakTimer = null
    }

    private fun shouldSuppressBreak(): Boolean {
        val paused = isPaused()
        val dnd = dndManager.isDndEnabled()
        val snoozed = System.currentTimeMillis() < snoozedUntilTimeMillis
        val withinWorkHours = GoalModeManager.isWithinWorkHours()
        return paused || dnd || snoozed || !withinWorkHours
    }
}
