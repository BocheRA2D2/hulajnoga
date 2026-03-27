package com.example.scooterhud.data

import android.content.Context
import android.content.SharedPreferences

data class LayoutState(
    val scale: Float = 1.0f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f
)

class LayoutSettings(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("layout_prefs", Context.MODE_PRIVATE)

    fun saveLayout(key: String, state: LayoutState) {
        prefs.edit().apply {
            putFloat("${key}_scale", state.scale)
            putFloat("${key}_offsetX", state.offsetX)
            putFloat("${key}_offsetY", state.offsetY)
            apply()
        }
    }

    fun loadLayout(key: String): LayoutState {
        return LayoutState(
            scale = prefs.getFloat("${key}_scale", 1.0f),
            offsetX = prefs.getFloat("${key}_offsetX", 0f),
            offsetY = prefs.getFloat("${key}_offsetY", 0f)
        )
    }

    fun reset() {
        prefs.edit().clear().apply()
    }
}
