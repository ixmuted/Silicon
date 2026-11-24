package com.silicon.data

data class RotationState(
    val isActive: Boolean = false,
    val orientation: OrientationMode = OrientationMode.LANDSCAPE,
    val defaultOrientation: OrientationMode = OrientationMode.LANDSCAPE,
    val returnOnCalls: Boolean = false,
    val autoDisableOnLeaveApp: Boolean = true,
    val showNotification: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = false,
    val isServiceConnected: Boolean = false,
    val isPausedForCall: Boolean = false
)
