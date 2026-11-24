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
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
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
        val expectedComponent = ComponentName(context, SiliconAccessibilityService::class.java)
        val am = context.getSystemService(AccessibilityManager::class.java)
        val enabledByManager = am?.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            ?.any {
                val serviceInfo = it.resolveInfo.serviceInfo
                serviceInfo.packageName == expectedComponent.packageName &&
                    serviceInfo.name == expectedComponent.className
            } == true
        if (enabledByManager) return true

        val enabledSetting = runCatching {
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
        }.getOrNull().orEmpty()
        return enabledSetting
            .split(':')
            .any { ComponentName.unflattenFromString(it.trim()) == expectedComponent }
    }

    fun isXiaomiDevice(): Boolean {
        val manufacturer = Build.MANUFACTURER.orEmpty()
        val brand = Build.BRAND.orEmpty()
        return manufacturer.equals("xiaomi", ignoreCase = true) ||
            brand.equals("xiaomi", ignoreCase = true) ||
            brand.equals("redmi", ignoreCase = true) ||
            brand.equals("poco", ignoreCase = true)
    }

    fun isBatteryOptimizationIgnored(context: Context): Boolean {
        val pm = context.getSystemService(PowerManager::class.java) ?: return false
        return pm.isIgnoringBatteryOptimizations(context.packageName)
    }

    fun requestIgnoreBatteryOptimizations(context: Context) {
        val requestIntent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:${context.packageName}")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if (tryStartActivity(context, requestIntent)) return
        val settingsIntent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if (tryStartActivity(context, settingsIntent)) return
        openAppDetailsSettings(context)
    }

    fun openMiuiAutostartSettings(context: Context): Boolean {
        val candidates = listOf(
            ComponentName(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity"
            ),
            ComponentName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"
            )
        )
        for (component in candidates) {
            val intent = Intent().apply {
                this.component = component
                putExtra("extra_pkgname", context.packageName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            if (tryStartActivity(context, intent)) return true
        }
        openAppDetailsSettings(context)
        return false
    }

    fun openAppDetailsSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        tryStartActivity(context, intent)
    }

    private fun tryStartActivity(context: Context, intent: Intent): Boolean {
        return runCatching {
            val resolved = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolved == null) return false
            context.startActivity(intent)
            true
        }.getOrDefault(false)
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
