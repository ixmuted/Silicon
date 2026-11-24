package com.silicon.ui

import android.Manifest
import android.app.StatusBarManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.silicon.service.RotationController
import com.silicon.ui.screens.HomeScreen
import com.silicon.ui.screens.SettingsScreen
import com.silicon.ui.screens.SetupScreen
import com.silicon.ui.theme.SiliconPalette
import com.silicon.ui.theme.SiliconTheme
import com.silicon.util.SystemIntegration
import kotlinx.coroutines.delay

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
    var pendingEnableReturnOnCalls by remember { mutableStateOf(false) }
    var tileAdded by rememberSaveable { mutableStateOf(false) }
    var successBannerText by remember { mutableStateOf<String?>(null) }
    var lastBannerText by remember { mutableStateOf("") }

    var routeName by rememberSaveable {
        mutableStateOf(
            if (openSetupInitially || !accessibilityEnabled) SiliconRoute.SETUP.name else SiliconRoute.HOME.name
        )
    }
    val route = when (routeName) {
        SiliconRoute.SETUP.name -> SiliconRoute.SETUP
        SiliconRoute.SETTINGS.name -> SiliconRoute.SETTINGS
        else -> SiliconRoute.HOME
    }

    LaunchedEffect(openSetupSignal) {
        if (openSetupSignal > 0) {
            routeName = SiliconRoute.SETUP.name
        }
    }

    LaunchedEffect(phonePermissionGranted, state.returnOnCalls) {
        if (!phonePermissionGranted && state.returnOnCalls) {
            RotationController.setReturnOnCalls(context, false)
        }
    }

    LaunchedEffect(successBannerText) {
        if (successBannerText != null) {
            lastBannerText = successBannerText.orEmpty()
            delay(2800)
            successBannerText = null
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                accessibilityEnabled = SystemIntegration.isAccessibilityEnabled(context)
                val hasPhone = SystemIntegration.hasPhoneStatePermission(context)
                phonePermissionGranted = hasPhone
                if (!hasPhone && RotationController.state.value.returnOnCalls) {
                    RotationController.setReturnOnCalls(context, false)
                }
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
        if (pendingEnableReturnOnCalls) {
            pendingEnableReturnOnCalls = false
            if (granted) {
                RotationController.setReturnOnCalls(context, true)
            } else {
                RotationController.setReturnOnCalls(context, false)
                Toast.makeText(
                    context,
                    "Phone permission is required for Return on calls",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (!granted && RotationController.state.value.returnOnCalls) {
            RotationController.setReturnOnCalls(context, false)
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        notificationPermissionGranted = granted
    }

    val handleReturnOnCallsChange: (Boolean) -> Unit = { enabled ->
        if (!enabled) {
            pendingEnableReturnOnCalls = false
            RotationController.setReturnOnCalls(context, false)
        } else if (SystemIntegration.hasPhoneStatePermission(context)) {
            phonePermissionGranted = true
            RotationController.setReturnOnCalls(context, true)
        } else {
            phonePermissionGranted = false
            pendingEnableReturnOnCalls = true
            phonePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
        }
    }

    val effectiveState = if (phonePermissionGranted) {
        state
    } else {
        state.copy(returnOnCalls = false)
    }

    val addTileAction: () -> Unit = {
        SystemIntegration.requestAddQuickSettingsTile(context) { result ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                when (result) {
                    StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED -> {
                        tileAdded = true
                        successBannerText = "Quick Settings tile added"
                    }
                    StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED -> {
                        tileAdded = true
                        successBannerText = "Tile already in Quick Settings"
                    }
                }
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
        routeName = SiliconRoute.HOME.name
    }

    SiliconTheme(themeMode = effectiveState.themeMode) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .widthIn(max = 560.dp)
                    .fillMaxSize(),
                targetState = route,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(120)) },
                label = "route_transition"
            ) { currentRoute ->
                when (currentRoute) {
                    SiliconRoute.HOME -> HomeScreen(
                        state = effectiveState,
                        accessibilityEnabled = accessibilityEnabled,
                        onToggleActive = { RotationController.toggle(context) },
                        onSelectOrientation = { RotationController.setOrientation(context, it, activate = true) },
                        onReturnOnCallsChange = handleReturnOnCallsChange,
                        onAutoDisableChange = { RotationController.setAutoDisableOnLeaveApp(context, it) },
                        onOpenSettings = { routeName = SiliconRoute.SETTINGS.name },
                        onOpenSetup = { routeName = SiliconRoute.SETUP.name }
                    )

                    SiliconRoute.SETUP -> SetupScreen(
                        state = effectiveState,
                        accessibilityEnabled = accessibilityEnabled,
                        phonePermissionGranted = phonePermissionGranted,
                        notificationPermissionGranted = notificationPermissionGranted,
                        tileAdded = tileAdded,
                        onBack = { routeName = SiliconRoute.HOME.name },
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
                        state = effectiveState,
                        accessibilityEnabled = accessibilityEnabled,
                        tileAdded = tileAdded,
                        onBack = { routeName = SiliconRoute.HOME.name },
                        onOpenSetup = { routeName = SiliconRoute.SETUP.name },
                        onAddQuickSettingsTile = addTileAction,
                        onPinBarWidget = {
                            if (!SystemIntegration.requestPinBarWidget(context)) {
                                Toast.makeText(context, "Add Silicon Bar from your launcher widgets", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onPinSquareWidget = {
                            if (!SystemIntegration.requestPinSquareWidget(context)) {
                                Toast.makeText(context, "Add Silicon Grid from your launcher widgets", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onThemeModeChange = { RotationController.setThemeMode(context, it) },
                        onDefaultOrientationChange = { RotationController.setDefaultOrientation(context, it) },
                        onReturnOnCallsChange = handleReturnOnCallsChange,
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

            AnimatedVisibility(
                visible = successBannerText != null,
                enter = fadeIn(tween(180)) + slideInVertically(tween(220)) { -it / 2 },
                exit = fadeOut(tween(160)) + slideOutVertically(tween(180)) { -it / 2 },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = SiliconPalette.Success,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = lastBannerText,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
        }
    }
}
