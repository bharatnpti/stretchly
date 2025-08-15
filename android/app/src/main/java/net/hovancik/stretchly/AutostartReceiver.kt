package net.hovancik.stretchly

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AutostartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val serviceIntent = Intent(context, BreakSchedulerService::class.java)
            context.startService(serviceIntent)
        }
    }
}
