package com.silicon.rotation.ui

import android.Manifest
import android.app.StatusBarManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.silicon.rotation.service.RotationController
import com.silicon.rotation.ui.screens.HomeScreen
import com.silicon.rotation.ui.screens.SettingsScreen
import com.silicon.rotation.ui.screens.SetupScreen
import com.silicon.rotation.ui.theme.SiliconTheme
import com.silicon.rotation.util.SystemIntegration

private enum class SiliconRoute {
    HOME,
    SETUP,
    SETTINGS
}

@Composable
fun SiliconNavApp(
    openSetupInitially: Boolean,
    openSetupSignal: Int = 0
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by RotationController.state.collectAsStateWithLifecycle()

    var accessibilityEnabled by remember {
        mutableStateOf(SystemIntegration.isAccessibilityEnabled(context))
    }
    var phonePermissionGranted by remember {
        mutableStateOf(SystemIntegration.hasPhoneStatePermission(context))
    }
    var notificationPermissionGranted by remember {
        mutableStateOf(SystemIntegration.hasNotificationPermission(context))
    }
    var tileAdded by rememberSaveable { mutableStateOf(false) }

    var route by rememberSaveable {
        mutableStateOf(
            if (openSetupInitially || !accessibilityEnabled) SiliconRoute.SETUP else SiliconRoute.HOME
        )
    }

    LaunchedEffect(openSetupSignal) {
        if (openSetupSignal > 0) {
            route = SiliconRoute.SETUP
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                accessibilityEnabled = SystemIntegration.isAccessibilityEnabled(context)
                phonePermissionGranted = SystemIntegration.hasPhoneStatePermission(context)
                notificationPermissionGranted = SystemIntegration.hasNotificationPermission(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val phonePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        phonePermissionGranted = granted
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        notificationPermissionGranted = granted
    }

    val addTileAction: () -> Unit = {
        SystemIntegration.requestAddQuickSettingsTile(context) { result ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                (result == StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED ||
                    result == StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED)
            ) {
                tileAdded = true
            } else if (result == -1) {
                Toast.makeText(
                    context,
                    "Add Silicon from your Quick Settings edit panel",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    BackHandler(enabled = route != SiliconRoute.HOME) {
        route = SiliconRoute.HOME
    }

    SiliconTheme(
        themeMode = state.themeMode,
        dynamicColor = state.dynamicColor
    ) {
        AnimatedContent(
            targetState = route,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "route_transition"
        ) { currentRoute ->
            when (currentRoute) {
                SiliconRoute.HOME -> HomeScreen(
                    state = state,
                    accessibilityEnabled = accessibilityEnabled,
                    onToggleActive = { RotationController.toggle(context) },
                    onSelectOrientation = { RotationController.setOrientation(context, it, activate = true) },
                    onReturnOnCallsChange = { enabled ->
                        RotationController.setReturnOnCalls(context, enabled)
                        if (enabled && !phonePermissionGranted) {
                            phonePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
                        }
                    },
                    onAutoDisableChange = { RotationController.setAutoDisableOnLeaveApp(context, it) },
                    onOpenSettings = { route = SiliconRoute.SETTINGS },
                    onOpenSetup = { route = SiliconRoute.SETUP }
                )

                SiliconRoute.SETUP -> SetupScreen(
                    state = state,
                    accessibilityEnabled = accessibilityEnabled,
                    phonePermissionGranted = phonePermissionGranted,
                    notificationPermissionGranted = notificationPermissionGranted,
                    tileAdded = tileAdded,
                    onBack = { route = SiliconRoute.HOME },
                    onEnableAccessibility = { SystemIntegration.openAccessibilitySettings(context) },
                    onAddQuickSettingsTile = addTileAction,
                    onRequestPhonePermission = {
                        phonePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
                    },
                    onRequestNotificationPermission = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                )

                SiliconRoute.SETTINGS -> SettingsScreen(
                    state = state,
                    accessibilityEnabled = accessibilityEnabled,
                    onBack = { route = SiliconRoute.HOME },
                    onOpenSetup = { route = SiliconRoute.SETUP },
                    onAddQuickSettingsTile = addTileAction,
                    onPinBarWidget = {
                        if (!SystemIntegration.requestPinBarWidget(context)) {
                            Toast.makeText(context, "Add Silicon Bar from your launcher widgets", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onPinSquareWidget = {
                        if (!SystemIntegration.requestPinSquareWidget(context)) {
                            Toast.makeText(context, "Add Silicon Square from your launcher widgets", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onThemeModeChange = { RotationController.setThemeMode(context, it) },
                    onDynamicColorChange = { RotationController.setDynamicColor(context, it) },
                    onDefaultOrientationChange = { RotationController.setDefaultOrientation(context, it) },
                    onReturnOnCallsChange = { enabled ->
                        RotationController.setReturnOnCalls(context, enabled)
                        if (enabled && !phonePermissionGranted) {
                            phonePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
                        }
                    },
                    onAutoDisableChange = { RotationController.setAutoDisableOnLeaveApp(context, it) },
                    onShowNotificationChange = { enabled ->
                        RotationController.setShowNotification(context, enabled)
                        if (enabled && !notificationPermissionGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                )
            }
        }
    }
}
