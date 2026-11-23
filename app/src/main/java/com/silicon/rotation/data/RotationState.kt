package com.silicon.rotation.data

data class RotationState(
    val isActive: Boolean = false,
    val orientation: OrientationMode = OrientationMode.LANDSCAPE,
    val defaultOrientation: OrientationMode = OrientationMode.LANDSCAPE,
    val returnOnCalls: Boolean = true,
    val autoDisableOnLeaveApp: Boolean = true,
    val showNotification: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val isServiceConnected: Boolean = false,
    val isPausedForCall: Boolean = false
)
