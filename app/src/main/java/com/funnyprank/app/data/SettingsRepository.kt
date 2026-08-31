package com.funnyprank.app.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Lightweight persistence for app settings and the floating overlay position.
 * Uses SharedPreferences (simple, synchronous, survives restart).
 */
class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("funny_prank_settings", Context.MODE_PRIVATE)

    var darkTheme: Boolean
        get() = prefs.getBoolean(KEY_THEME, true)
        set(value) = prefs.edit().putBoolean(KEY_THEME, value).apply()

    var audioPreview: Boolean
        get() = prefs.getBoolean(KEY_PREVIEW, true)
        set(value) = prefs.edit().putBoolean(KEY_PREVIEW, value).apply()

    var floatingControl: Boolean
        get() = prefs.getBoolean(KEY_FLOAT, false)
        set(value) = prefs.edit().putBoolean(KEY_FLOAT, value).apply()

    var onboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING, value).apply()

    var overlayX: Int
        get() = prefs.getInt(KEY_OVERLAY_X, Int.MIN_VALUE)
        set(value) = prefs.edit().putInt(KEY_OVERLAY_X, value).apply()

    var overlayY: Int
        get() = prefs.getInt(KEY_OVERLAY_Y, Int.MIN_VALUE)
        set(value) = prefs.edit().putInt(KEY_OVERLAY_Y, value).apply()

    fun hasStoredOverlayPosition(): Boolean = overlayX != Int.MIN_VALUE && overlayY != Int.MIN_VALUE

    fun clearOverlayPosition() {
        prefs.edit()
            .remove(KEY_OVERLAY_X)
            .remove(KEY_OVERLAY_Y)
            .apply()
    }

    private companion object {
        const val KEY_THEME = "dark_theme"
        const val KEY_PREVIEW = "audio_preview"
        const val KEY_FLOAT = "floating_control"
        const val KEY_ONBOARDING = "onboarding_completed"
        const val KEY_OVERLAY_X = "overlay_x"
        const val KEY_OVERLAY_Y = "overlay_y"
    }
}
