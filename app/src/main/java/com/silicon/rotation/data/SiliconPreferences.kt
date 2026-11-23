package com.silicon.rotation.data

import android.content.Context
import android.content.SharedPreferences

class SiliconPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadState(): RotationState {
        return RotationState(
            isActive = prefs.getBoolean(KEY_ACTIVE, false),
            orientation = OrientationMode.fromId(prefs.getString(KEY_ORIENTATION, OrientationMode.LANDSCAPE.id)),
            defaultOrientation = OrientationMode.fromId(prefs.getString(KEY_DEFAULT_ORIENTATION, OrientationMode.LANDSCAPE.id)),
            returnOnCalls = prefs.getBoolean(KEY_RETURN_ON_CALLS, true),
            autoDisableOnLeaveApp = prefs.getBoolean(KEY_AUTO_DISABLE_LEAVE, true),
            showNotification = prefs.getBoolean(KEY_SHOW_NOTIFICATION, true),
            themeMode = ThemeMode.fromId(prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.id)),
            dynamicColor = prefs.getBoolean(KEY_DYNAMIC_COLOR, false)
        )
    }

    fun saveActive(active: Boolean) {
        prefs.edit().putBoolean(KEY_ACTIVE, active).apply()
    }

    fun saveOrientation(mode: OrientationMode) {
        prefs.edit().putString(KEY_ORIENTATION, mode.id).apply()
    }

    fun saveDefaultOrientation(mode: OrientationMode) {
        prefs.edit().putString(KEY_DEFAULT_ORIENTATION, mode.id).apply()
    }

    fun saveReturnOnCalls(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_RETURN_ON_CALLS, enabled).apply()
    }

    fun saveAutoDisableOnLeaveApp(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_DISABLE_LEAVE, enabled).apply()
    }

    fun saveShowNotification(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_NOTIFICATION, enabled).apply()
    }

    fun saveThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.id).apply()
    }

    fun saveDynamicColor(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DYNAMIC_COLOR, enabled).apply()
    }

    companion object {
        private const val PREFS_NAME = "silicon_prefs"
        private const val KEY_ACTIVE = "active"
        private const val KEY_ORIENTATION = "orientation"
        private const val KEY_DEFAULT_ORIENTATION = "default_orientation"
        private const val KEY_RETURN_ON_CALLS = "return_on_calls"
        private const val KEY_AUTO_DISABLE_LEAVE = "auto_disable_leave"
        private const val KEY_SHOW_NOTIFICATION = "show_notification"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_DYNAMIC_COLOR = "dynamic_color"
    }
}
