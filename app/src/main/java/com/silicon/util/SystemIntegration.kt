package com.silicon.util

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.StatusBarManager
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Build
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.core.content.ContextCompat
import com.silicon.R
import com.silicon.service.SiliconAccessibilityService
import com.silicon.service.SiliconTileService
import com.silicon.widget.SiliconBarWidgetReceiver
import com.silicon.widget.SiliconSquareWidgetReceiver

object SystemIntegration {

    fun isAccessibilityEnabled(context: Context): Boolean {
        val am = context.getSystemService(AccessibilityManager::class.java) ?: return false
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        val expectedComponent = ComponentName(context, SiliconAccessibilityService::class.java)
        return enabledServices.any {
            val serviceInfo = it.resolveInfo.serviceInfo
            serviceInfo.packageName == expectedComponent.packageName &&
                serviceInfo.name == expectedComponent.className
        }
    }

    fun hasPhoneStatePermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasNotificationPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun openAccessibilitySettings(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun requestAddQuickSettingsTile(context: Context, onResult: (Int) -> Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            onResult(-1)
            return
        }
        val statusBarManager = context.getSystemService(StatusBarManager::class.java)
        if (statusBarManager == null) {
            onResult(-1)
            return
        }
        statusBarManager.requestAddTileService(
            ComponentName(context, SiliconTileService::class.java),
            context.getString(R.string.app_name),
            Icon.createWithResource(context, R.drawable.ic_silicon_tile),
            context.mainExecutor
        ) { resultCode ->
            onResult(resultCode)
        }
    }

    fun requestPinBarWidget(context: Context): Boolean {
        return requestPinWidget(context, SiliconBarWidgetReceiver::class.java)
    }

    fun requestPinSquareWidget(context: Context): Boolean {
        return requestPinWidget(context, SiliconSquareWidgetReceiver::class.java)
    }

    private fun requestPinWidget(context: Context, receiverClass: Class<*>): Boolean {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        if (!appWidgetManager.isRequestPinAppWidgetSupported) return false
        val provider = ComponentName(context, receiverClass)
        return appWidgetManager.requestPinAppWidget(provider, null, null)
    }
}
