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
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityManager
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import com.silicon.R
import com.silicon.service.SiliconAccessibilityService
import com.silicon.service.SiliconTileService
import com.silicon.widget.SiliconBarWidgetReceiver
import com.silicon.widget.SiliconSquareWidgetReceiver

object SystemIntegration {

    fun isAccessibilityEnabled(context: Context): Boolean {
        val am = context.getSystemService(AccessibilityManager::class.java)
        val expectedComponent = ComponentName(context, SiliconAccessibilityService::class.java)
        val expectedFlat = expectedComponent.flattenToString()
        val expectedShort = expectedComponent.flattenToShortString()

        if (am != null) {
            val enabledServices = runCatching {
                am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            }.getOrDefault(emptyList())
            val boundInManager = enabledServices.any {
                val serviceInfo = it.resolveInfo?.serviceInfo ?: return@any false
                serviceInfo.packageName == expectedComponent.packageName &&
                    serviceInfo.name == expectedComponent.className
            }
            if (boundInManager) return true
        }

        val accessibilityEnabled = runCatching {
            Settings.Secure.getInt(context.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED, 0)
        }.getOrDefault(0)
        if (accessibilityEnabled != 1) return false

        val enabledSetting = runCatching {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        }.getOrNull().orEmpty()

        if (enabledSetting.isBlank()) return false
        val splitter = TextUtils.SimpleStringSplitter(':')
        splitter.setString(enabledSetting)
        while (splitter.hasNext()) {
            val componentString = splitter.next()
            if (componentString.equals(expectedFlat, ignoreCase = true) ||
                componentString.equals(expectedShort, ignoreCase = true)
            ) {
                return true
            }
        }
        return false
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
        return requestPinWidget(
            context = context,
            receiverClass = SiliconBarWidgetReceiver::class.java,
            previewLayoutRes = R.layout.widget_bar_preview
        )
    }

    fun requestPinSquareWidget(context: Context): Boolean {
        return requestPinWidget(
            context = context,
            receiverClass = SiliconSquareWidgetReceiver::class.java,
            previewLayoutRes = R.layout.widget_square_preview
        )
    }

    private fun requestPinWidget(
        context: Context,
        receiverClass: Class<*>,
        @LayoutRes previewLayoutRes: Int
    ): Boolean {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        if (!appWidgetManager.isRequestPinAppWidgetSupported) return false
        val provider = ComponentName(context, receiverClass)
        val extras = Bundle().apply {
            putParcelable(
                AppWidgetManager.EXTRA_APPWIDGET_PREVIEW,
                RemoteViews(context.packageName, previewLayoutRes)
            )
        }
        return appWidgetManager.requestPinAppWidget(provider, extras, null)
    }
}
