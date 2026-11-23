package com.silicon.rotation.ui

import android.Manifest
import android.app.StatusBarManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
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
import com.silicon.rotation.service.RotationController
import com.silicon.rotation.ui.screens.HomeScreen
import com.silicon.rotation.ui.screens.SettingsScreen
import com.silicon.rotation.ui.screens.SetupScreen
import com.silicon.rotation.ui.theme.SiliconTheme
import com.silicon.rotation.util.SystemIntegration
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
    var tileAdded by rememberSaveable { mutableStateOf(false) }
    var successBannerText by remember { mutableStateOf<String?>(null) }

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

    LaunchedEffect(successBannerText) {
        if (successBannerText != null) {
            delay(2800)
            successBannerText = null
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

    SiliconTheme(
        themeMode = state.themeMode,
        dynamicColor = state.dynamicColor
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = route,
                transitionSpec = {
                    val forward = targetState != SiliconRoute.HOME
                    (fadeIn(animationSpec = tween(220)) + slideInHorizontally(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) { width -> if (forward) width / 8 else -width / 8 }) togetherWith
                        (fadeOut(animationSpec = tween(160)) + slideOutHorizontally(
                            animationSpec = tween(180)
                        ) { width -> if (forward) -width / 10 else width / 10 })
                },
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
                        onOpenSettings = { routeName = SiliconRoute.SETTINGS.name },
                        onOpenSetup = { routeName = SiliconRoute.SETUP.name }
                    )

                    SiliconRoute.SETUP -> SetupScreen(
                        state = state,
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
                        state = state,
                        accessibilityEnabled = accessibilityEnabled,
                        tileAdded = tileAdded,
                        onBack = { routeName = SiliconRoute.HOME.name },
                        onOpenSetup = { routeName = SiliconRoute.SETUP.name },
                        onAddQuickSettingsTile = addTileAction,
                        onPinBarWidget = {
                            if (!SystemIntegration.requestPinBarWidget(context)) {
                                Toast.makeText(context, "Add Silicon Bar from your launcher widgets", Toast.LENGTH_SHORT).show()
                            } else {
                                successBannerText = "Bar widget pinned"
                            }
                        },
                        onPinSquareWidget = {
                            if (!SystemIntegration.requestPinSquareWidget(context)) {
                                Toast.makeText(context, "Add Silicon Square from your launcher widgets", Toast.LENGTH_SHORT).show()
                            } else {
                                successBannerText = "Grid widget pinned"
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

            // Floating animated success pill for Quick Settings tile & widget actions
            AnimatedVisibility(
                visible = successBannerText != null,
                enter = slideInVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) { -it } + scaleIn(
                    initialScale = 0.88f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn(tween(180)),
                exit = slideOutVertically(tween(200)) { -it } + scaleOut(targetScale = 0.92f) + fadeOut(tween(180)),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.35f)),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = successBannerText.orEmpty(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }
    }
}
