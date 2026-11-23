package com.silicon.rotation.service

import android.content.ComponentName
import android.content.Context
import android.service.quicksettings.TileService
import com.silicon.rotation.data.OrientationMode
import com.silicon.rotation.data.RotationState
import com.silicon.rotation.data.SiliconPreferences
import com.silicon.rotation.data.ThemeMode
import com.silicon.rotation.widget.SiliconWidgetUpdater
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object RotationController {

    private var preferences: SiliconPreferences? = null
    private val _state = MutableStateFlow(RotationState())
    val state: StateFlow<RotationState> = _state.asStateFlow()

    @Volatile
    private var serviceRef: SiliconAccessibilityService? = null

    fun initialize(context: Context) {
        if (preferences != null) return
        val prefs = SiliconPreferences(context)
        preferences = prefs
        val loaded = prefs.loadState()
        _state.value = loaded.copy(isServiceConnected = serviceRef != null)
    }

    fun attachService(service: SiliconAccessibilityService) {
        initialize(service)
        serviceRef = service
        _state.update { it.copy(isServiceConnected = true) }
        syncAll(service)
    }

    fun detachService(context: Context) {
        serviceRef = null
        _state.update { it.copy(isServiceConnected = false, isPausedForCall = false) }
        SiliconNotificationManager.cancel(context)
        requestSystemSync(context)
    }

    fun toggle(context: Context): Boolean {
        initialize(context)
        val current = _state.value
        return if (current.isActive) {
            disable(context)
            false
        } else {
            enable(context, current.defaultOrientation)
            true
        }
    }

    fun enable(context: Context, orientation: OrientationMode = _state.value.orientation) {
        initialize(context)
        preferences?.saveOrientation(orientation)
        preferences?.saveActive(true)
        _state.update {
            it.copy(
                isActive = true,
                orientation = orientation,
                isPausedForCall = false
            )
        }
        syncAll(context)
    }

    fun disable(context: Context) {
        initialize(context)
        preferences?.saveActive(false)
        _state.update { it.copy(isActive = false, isPausedForCall = false) }
        syncAll(context)
    }

    fun setOrientation(context: Context, orientation: OrientationMode, activate: Boolean = true) {
        initialize(context)
        preferences?.saveOrientation(orientation)
        if (activate) {
            preferences?.saveActive(true)
        }
        _state.update {
            it.copy(
                orientation = orientation,
                isActive = if (activate) true else it.isActive,
                isPausedForCall = false
            )
        }
        syncAll(context)
    }

    fun cycleOrientation(context: Context) {
        initialize(context)
        val current = _state.value
        val next = if (current.isActive) current.orientation.next() else current.defaultOrientation
        setOrientation(context, next, activate = true)
    }

    fun setDefaultOrientation(context: Context, orientation: OrientationMode) {
        initialize(context)
        preferences?.saveDefaultOrientation(orientation)
        _state.update { it.copy(defaultOrientation = orientation) }
    }

    fun setReturnOnCalls(context: Context, enabled: Boolean) {
        initialize(context)
        preferences?.saveReturnOnCalls(enabled)
        _state.update { it.copy(returnOnCalls = enabled) }
        serviceRef?.syncOverlayState(_state.value)
    }

    fun setAutoDisableOnLeaveApp(context: Context, enabled: Boolean) {
        initialize(context)
        preferences?.saveAutoDisableOnLeaveApp(enabled)
        _state.update { it.copy(autoDisableOnLeaveApp = enabled) }
        serviceRef?.syncOverlayState(_state.value)
    }

    fun setShowNotification(context: Context, enabled: Boolean) {
        initialize(context)
        preferences?.saveShowNotification(enabled)
        _state.update { it.copy(showNotification = enabled) }
        val current = _state.value
        SiliconNotificationManager.update(context, current.isActive && current.isServiceConnected, current.orientation, enabled)
    }

    fun setThemeMode(context: Context, mode: ThemeMode) {
        initialize(context)
        preferences?.saveThemeMode(mode)
        _state.update { it.copy(themeMode = mode) }
    }

    fun setDynamicColor(context: Context, enabled: Boolean) {
        initialize(context)
        preferences?.saveDynamicColor(enabled)
        _state.update { it.copy(dynamicColor = enabled) }
    }

    internal fun onCallStateChanged(context: Context, inCall: Boolean) {
        val current = _state.value
        if (!current.isActive || !current.returnOnCalls) return
        if (inCall) {
            if (!current.isPausedForCall) {
                _state.update {
                    it.copy(
                        isPausedForCall = true,
                        orientation = OrientationMode.PORTRAIT
                    )
                }
                preferences?.saveOrientation(OrientationMode.PORTRAIT)
                syncAll(context)
            }
        } else if (current.isPausedForCall) {
            _state.update { it.copy(isPausedForCall = false) }
            syncAll(context)
        }
    }

    private fun syncAll(context: Context) {
        val current = _state.value
        serviceRef?.syncOverlayState(current)
        SiliconNotificationManager.update(
            context = context,
            isActive = current.isActive && current.isServiceConnected,
            orientation = current.orientation,
            enabled = current.showNotification
        )
        requestSystemSync(context)
    }

    private fun requestSystemSync(context: Context) {
        runCatching {
            TileService.requestListeningState(
                context,
                ComponentName(context, SiliconTileService::class.java)
            )
        }
        SiliconWidgetUpdater.updateAll(context)
    }
}
