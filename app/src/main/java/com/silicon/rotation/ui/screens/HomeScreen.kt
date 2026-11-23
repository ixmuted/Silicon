package com.silicon.rotation.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.silicon.rotation.R
import com.silicon.rotation.data.OrientationMode
import com.silicon.rotation.data.RotationState
import com.silicon.rotation.ui.components.ModernPhoneIcon
import com.silicon.rotation.ui.components.SettingsGroupCard
import com.silicon.rotation.ui.components.SettingsGroupDivider
import com.silicon.rotation.ui.components.SettingsToggleRow
import com.silicon.rotation.ui.components.bounceClick

@Composable
fun HomeScreen(
    state: RotationState,
    accessibilityEnabled: Boolean,
    onToggleActive: () -> Unit,
    onSelectOrientation: (OrientationMode) -> Unit,
    onReturnOnCallsChange: (Boolean) -> Unit,
    onAutoDisableChange: (Boolean) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenSetup: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Minimal top bar: original colorful Silicon emblem + wordmark + settings button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_silicon_emblem),
                contentDescription = "Silicon",
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = "Silicon",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            Surface(
                modifier = Modifier
                    .size(38.dp)
                    .bounceClick(onClick = onOpenSettings),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainer,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.Tune,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        if (!accessibilityEnabled) {
            SetupPromptBanner(onOpenSetup = onOpenSetup)
        }

        HeroControlCard(
            state = state,
            accessibilityEnabled = accessibilityEnabled,
            onPrimaryAction = {
                if (accessibilityEnabled) onToggleActive() else onOpenSetup()
            }
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "ORIENTATION",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp)
            )
            OrientationGrid(
                active = state.isActive,
                selected = state.orientation,
                onSelect = { mode ->
                    if (accessibilityEnabled) onSelectOrientation(mode) else onOpenSetup()
                }
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "AUTOMATION",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp)
            )
            SettingsGroupCard {
                SettingsToggleRow(
                    icon = Icons.Rounded.Call,
                    title = "Return on calls",
                    subtitle = "Portrait when a call arrives",
                    checked = state.returnOnCalls,
                    onCheckedChange = onReturnOnCallsChange
                )
                SettingsGroupDivider()
                SettingsToggleRow(
                    icon = Icons.AutoMirrored.Rounded.ExitToApp,
                    title = "Stop on app switch",
                    subtitle = "Disable when leaving the app",
                    checked = state.autoDisableOnLeaveApp,
                    onCheckedChange = onAutoDisableChange
                )
            }
        }
    }
}

@Composable
private fun SetupPromptBanner(onOpenSetup: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick(onClick = onOpenSetup),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.VerifiedUser,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Finish setup",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Enable rotation service to unlock controls",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun HeroControlCard(
    state: RotationState,
    accessibilityEnabled: Boolean,
    onPrimaryAction: () -> Unit
) {
    val isActive = state.isActive && accessibilityEnabled
    val cardBorder by animateColorAsState(
        targetValue = if (isActive) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
        } else {
            MaterialTheme.colorScheme.outlineVariant
        },
        animationSpec = tween(260),
        label = "hero_border"
    )
    val iconTint by animateColorAsState(
        targetValue = if (isActive) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(260),
        label = "hero_icon_tint"
    )
    val iconRotation by animateFloatAsState(
        targetValue = if (isActive) state.orientation.iconDegrees else 0f,
        animationSpec = spring(
            dampingRatio = 0.65f,
            stiffness = 260f
        ),
        label = "hero_rotation"
    )

    val pulse = rememberInfiniteTransition(label = "status_pulse")
    val dotAlpha by pulse.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(1.dp, cardBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .alpha(if (isActive) dotAlpha else 0.35f)
                                .background(
                                    if (isActive) MaterialTheme.colorScheme.tertiary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        )
                        Text(
                            text = if (isActive) "ACTIVE" else "STANDBY",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    AnimatedContent(
                        targetState = when {
                            !accessibilityEnabled -> "Setup needed"
                            isActive -> state.orientation.label
                            else -> "Off"
                        },
                        transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(120)) },
                        label = "hero_title"
                    ) { label ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = when {
                            !accessibilityEnabled -> "Service not enabled"
                            isActive -> "Locked across apps"
                            else -> "Default: ${state.defaultOrientation.shortLabel}"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    modifier = Modifier.size(54.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        ModernPhoneIcon(
                            tint = iconTint,
                            rotationDegrees = iconRotation,
                            size = 28.dp,
                            filledScreen = isActive
                        )
                    }
                }
            }

            Button(
                onClick = onPrimaryAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.PowerSettingsNew,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = when {
                        !accessibilityEnabled -> "Open setup"
                        isActive -> "Turn off"
                        else -> "Turn on"
                    },
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun OrientationGrid(
    active: Boolean,
    selected: OrientationMode,
    onSelect: (OrientationMode) -> Unit
) {
    val rows = OrientationMode.entries.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { mode ->
                    OrientationTile(
                        mode = mode,
                        selected = active && selected == mode,
                        onClick = { onSelect(mode) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun OrientationTile(
    mode: OrientationMode,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        },
        animationSpec = tween(200),
        label = "tile_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outlineVariant
        },
        animationSpec = tween(200),
        label = "tile_border"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(200),
        label = "tile_fg"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.06f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tile_scale"
    )

    Surface(
        modifier = modifier
            .height(68.dp)
            .bounceClick(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ModernPhoneIcon(
                tint = contentColor,
                rotationDegrees = mode.iconDegrees,
                size = 21.dp,
                filledScreen = selected,
                modifier = Modifier.graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                }
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = mode.label,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor,
                maxLines = 1
            )
        }
    }
}
