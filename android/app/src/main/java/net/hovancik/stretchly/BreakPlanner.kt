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

    fun scheduleBreaks() {
        if (dndManager.isDndEnabled()) {
            Log.d("BreakPlanner", "DND mode is enabled, not scheduling breaks")
            return
        }
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
                    Log.d("BreakPlanner", "Time for a microbreak!")
                    service.showBreakNotification("Time for a microbreak!", "microbreak")
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
                    Log.d("BreakPlanner", "Time for a break!")
                    service.showBreakNotification("Time for a break!", "break")
                    updateNextBreakTime()
                }
            }
        }, interval, interval)
    }

    private fun updateNextBreakTime() {
        val microbreakInterval = settingsManager.getLong("microbreakInterval", DefaultSettings.MICROBREAK_INTERVAL.toLong())
        val breakInterval = settingsManager.getLong("breakInterval", DefaultSettings.BREAK_INTERVAL.toLong())
        val nextMicrobreakTime = System.currentTimeMillis() + microbreakInterval
        val nextBreakTime = System.currentTimeMillis() + breakInterval

        this.nextBreakTime = if (settingsManager.getBoolean("microbreakEnabled", DefaultSettings.MICROBREAK_ENABLED) &&
            (!settingsManager.getBoolean("breakEnabled", DefaultSettings.BREAK_ENABLED) || nextMicrobreakTime < nextBreakTime)) {
            nextMicrobreakTime
        } else {
            nextBreakTime
        }
        updateNotification()
    }

    private fun updateNotification() {
        val timeRemaining = nextBreakTime - System.currentTimeMillis()
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemaining)
        service.updateNotification("Next break in $minutes minutes")
    }

    fun stop() {
        microbreakTimer?.cancel()
        breakTimer?.cancel()
    }
}
