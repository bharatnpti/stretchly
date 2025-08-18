package net.hovancik.stretchly

import android.app.Application

class StretchlyApplication : Application() {
    
    companion object {
        lateinit var instance: StretchlyApplication
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
