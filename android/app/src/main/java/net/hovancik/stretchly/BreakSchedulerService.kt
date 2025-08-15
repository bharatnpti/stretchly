package net.hovancik.stretchly

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class BreakSchedulerService : Service() {

    private lateinit var breakPlanner: BreakPlanner
    private val CHANNEL_ID = "StretchlyBreakChannel"
    private val NOTIFICATION_ID = 1

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("BreakSchedulerService", "Service started")
        createNotificationChannel()
        val notification = createNotification("Stretchly is running")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
        breakPlanner = BreakPlanner(this)
        handleAction(intent)
        breakPlanner.scheduleBreaks()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        breakPlanner.stop()
        Log.d("BreakSchedulerService", "Service destroyed")
    }

    fun showBreakNotification(message: String, breakType: String) {
        val intent = if (breakType == "microbreak") {
            Intent(this, MicrobreakActivity::class.java).apply {
                val duration = SettingsManager(this@BreakSchedulerService)
                    .getLong("microbreakDuration", DefaultSettings.MICROBREAK_DURATION.toLong())
                putExtra("duration", duration)
            }
        } else {
            Intent(this, BreakActivity::class.java).apply {
                val duration = SettingsManager(this@BreakSchedulerService)
                    .getLong("breakDuration", DefaultSettings.BREAK_DURATION.toLong())
                putExtra("duration", duration)
            }
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Stretchly")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    fun updateNotification(text: String) {
        val notification = createNotification(text)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotification(text: String): Notification {
        val openIntent = Intent(this, MainActivity::class.java)
        val openPending = PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_IMMUTABLE)

        val pauseIntent = Intent(this, BreakSchedulerService::class.java).setAction(ACTION_PAUSE)
        val pausePending = PendingIntent.getService(this, 1, pauseIntent, PendingIntent.FLAG_IMMUTABLE)

        val resumeIntent = Intent(this, BreakSchedulerService::class.java).setAction(ACTION_RESUME)
        val resumePending = PendingIntent.getService(this, 2, resumeIntent, PendingIntent.FLAG_IMMUTABLE)

        val snoozeIntent = Intent(this, BreakSchedulerService::class.java).setAction(ACTION_SNOOZE_10)
        val snoozePending = PendingIntent.getService(this, 3, snoozeIntent, PendingIntent.FLAG_IMMUTABLE)

        val skipMicroIntent = Intent(this, BreakSchedulerService::class.java).setAction(ACTION_SKIP_MICRO)
        val skipMicroPending = PendingIntent.getService(this, 4, skipMicroIntent, PendingIntent.FLAG_IMMUTABLE)

        val skipBreakIntent = Intent(this, BreakSchedulerService::class.java).setAction(ACTION_SKIP_BREAK)
        val skipBreakPending = PendingIntent.getService(this, 5, skipBreakIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Stretchly")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(openPending)
            .addAction(0, "Pause", pausePending)
            .addAction(0, "Resume", resumePending)
            .addAction(0, "Snooze 10m", snoozePending)
            .addAction(0, "Skip micro", skipMicroPending)
            .addAction(0, "Skip break", skipBreakPending)
            .build()
    }

    private fun handleAction(intent: Intent?) {
        when (intent?.action) {
            ACTION_PAUSE -> {
                breakPlanner.setPaused(true)
                breakPlanner.reschedule()
                updateNotification("Breaks paused")
            }
            ACTION_RESUME -> {
                breakPlanner.setPaused(false)
                breakPlanner.reschedule()
            }
            ACTION_SNOOZE_10 -> {
                breakPlanner.snooze(10)
            }
            ACTION_SKIP_MICRO -> {
                breakPlanner.skipOnce("microbreak")
            }
            ACTION_SKIP_BREAK -> {
                breakPlanner.skipOnce("break")
            }
            ACTION_RESCHEDULE -> {
                breakPlanner.reschedule()
            }
        }
    }

    companion object {
        const val ACTION_PAUSE = "net.hovancik.stretchly.action.PAUSE"
        const val ACTION_RESUME = "net.hovancik.stretchly.action.RESUME"
        const val ACTION_SNOOZE_10 = "net.hovancik.stretchly.action.SNOOZE_10"
        const val ACTION_SKIP_MICRO = "net.hovancik.stretchly.action.SKIP_MICRO"
        const val ACTION_SKIP_BREAK = "net.hovancik.stretchly.action.SKIP_BREAK"
        const val ACTION_RESCHEDULE = "net.hovancik.stretchly.action.RESCHEDULE"
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Stretchly"
            val descriptionText = "Channel for Stretchly break notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
