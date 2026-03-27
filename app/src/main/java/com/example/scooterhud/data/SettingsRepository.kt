package com.example.scooterhud.data

import android.content.Context

class SettingsRepository(context: Context) {

    private val prefs = context.getSharedPreferences("scooter_hud_prefs", Context.MODE_PRIVATE)

    var isAutoPauseEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTO_PAUSE, false)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_PAUSE, value).apply()

    var isDarkTheme: Boolean
        get() = prefs.getBoolean(KEY_DARK_THEME, true)
        set(value) = prefs.edit().putBoolean(KEY_DARK_THEME, value).apply()

    var isPortrait: Boolean
        get() = prefs.getBoolean(KEY_PORTRAIT, false)
        set(value) = prefs.edit().putBoolean(KEY_PORTRAIT, value).apply()

    companion object {
        private const val KEY_AUTO_PAUSE = "auto_pause"
        private const val KEY_DARK_THEME = "dark_theme"
        private const val KEY_PORTRAIT = "is_portrait"
    }
}
