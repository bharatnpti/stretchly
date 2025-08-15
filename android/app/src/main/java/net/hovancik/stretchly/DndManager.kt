package net.hovancik.stretchly

import android.app.NotificationManager
import android.content.Context
import android.os.Build

class DndManager(private val context: Context) {

    fun isDndEnabled(): Boolean {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL
        } else {
            false
        }
    }
}
