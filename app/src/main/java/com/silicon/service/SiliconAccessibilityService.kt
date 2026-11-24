package com.silicon.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import com.silicon.data.OrientationMode
import com.silicon.data.RotationState

class SiliconAccessibilityService : AccessibilityService() {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var overlayParams: WindowManager.LayoutParams? = null
    private var callStateMonitor: CallStateMonitor? = null
    private var anchoredPackage: String? = null
    private var lastObservedAppPackage: String? = null
    private var cachedImePackage: String? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        windowManager = getSystemService(WindowManager::class.java)
        cachedImePackage = resolveCurrentImePackage()
        callStateMonitor = CallStateMonitor(this) { inCall ->
            RotationController.onCallStateChanged(this, inCall)
        }
        RotationController.attachService(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = event.packageName?.toString()?.takeIf { it.isNotBlank() } ?: return
        if (isIgnoredPackage(pkg)) return
        val state = RotationController.state.value
        if (!state.isActive || !state.autoDisableOnLeaveApp) {
            lastObservedAppPackage = pkg
            return
        }
        val anchor = anchoredPackage
        if (anchor == null) {
            anchoredPackage = pkg
            lastObservedAppPackage = pkg
            return
        }
        if (pkg != anchor) {
            anchoredPackage = null
            lastObservedAppPackage = pkg
            RotationController.disable(this)
        }
    }

    override fun onInterrupt() {
    }

    override fun onUnbind(intent: Intent?): Boolean {
        teardown()
        return super.onUnbind(intent)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val state = RotationController.state.value
        if (state.isActive) {
            syncOverlayState(state)
        }
    }

    override fun onDestroy() {
        teardown()
        super.onDestroy()
    }

    fun syncOverlayState(state: RotationState) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            applySyncOverlayState(state)
        } else {
            mainHandler.post { applySyncOverlayState(state) }
        }
    }

    private fun applySyncOverlayState(state: RotationState) {
        if (state.isActive) {
            val targetOrientation = if (state.isPausedForCall) {
                OrientationMode.PORTRAIT
            } else {
                state.orientation
            }
            applyOverlay(targetOrientation.activityInfoValue)
            if (state.showNotification) {
                SiliconNotificationManager.startForeground(this, targetOrientation)
            } else {
                SiliconNotificationManager.stopForeground(this)
            }
            if (state.autoDisableOnLeaveApp) {
                if (anchoredPackage == null) {
                    anchoredPackage = lastObservedAppPackage?.takeUnless { isIgnoredPackage(it) }
                }
                updateEventMonitoring(true)
            } else {
                anchoredPackage = null
                updateEventMonitoring(false)
            }
            if (state.returnOnCalls) {
                callStateMonitor?.start()
            } else {
                callStateMonitor?.stop()
            }
        } else {
            removeOverlay()
            SiliconNotificationManager.stopForeground(this)
            anchoredPackage = null
            updateEventMonitoring(false)
            callStateMonitor?.stop()
        }
    }

    private fun applyOverlay(screenOrientation: Int) {
        val wm = windowManager ?: return
        val existingView = overlayView
        val existingParams = overlayParams
        if (existingView != null && existingParams != null) {
            if (existingParams.screenOrientation != screenOrientation) {
                existingParams.screenOrientation = screenOrientation
                runCatching { wm.updateViewLayout(existingView, existingParams) }
            }
            return
        }
        val params = WindowManager.LayoutParams(
            0,
            0,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            this.screenOrientation = screenOrientation
        }
        val view = View(this)
        runCatching {
            wm.addView(view, params)
            overlayView = view
            overlayParams = params
        }
    }

    private fun removeOverlay() {
        val wm = windowManager ?: return
        val view = overlayView ?: return
        runCatching { wm.removeViewImmediate(view) }
        overlayView = null
        overlayParams = null
    }

    private fun updateEventMonitoring(enable: Boolean) {
        val info = serviceInfo ?: AccessibilityServiceInfo()
        val desiredTypes = if (enable) AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED else 0
        if (info.eventTypes != desiredTypes) {
            info.eventTypes = desiredTypes
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            info.notificationTimeout = 100
            runCatching { serviceInfo = info }
        }
    }

    private fun isIgnoredPackage(pkg: String): Boolean {
        if (pkg == packageName) return true
        if (pkg in IGNORED_PACKAGES) return true
        val ime = cachedImePackage ?: resolveCurrentImePackage().also { cachedImePackage = it }
        return pkg == ime
    }

    private fun resolveCurrentImePackage(): String? {
        val raw = runCatching {
            Settings.Secure.getString(contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
        }.getOrNull() ?: return null
        return raw.substringBefore('/').takeIf { it.isNotBlank() }
    }

    private fun teardown() {
        mainHandler.removeCallbacksAndMessages(null)
        callStateMonitor?.stop()
        removeOverlay()
        SiliconNotificationManager.stopForeground(this)
        anchoredPackage = null
        RotationController.detachService(this)
    }

    companion object {
        private val IGNORED_PACKAGES = setOf(
            "com.android.systemui",
            "android",
            "com.google.android.permissioncontroller",
            "com.android.permissioncontroller",
            "com.google.android.inputmethod.latin"
        )
    }
}
