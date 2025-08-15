package net.hovancik.stretchly

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("StretchlySettings", Context.MODE_PRIVATE)

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getLong(key: String, defaultValue: Long): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    fun putLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }
}
