package com.silicon.rotation.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.unit.dp
import com.silicon.rotation.data.OrientationMode
import com.silicon.rotation.data.RotationState
import com.silicon.rotation.ui.components.ModernPhoneIcon
import com.silicon.rotation.ui.components.SettingsGroupCard
import com.silicon.rotation.ui.components.SettingsGroupDivider
import com.silicon.rotation.ui.components.SettingsToggleRow
import com.silicon.rotation.ui.components.SiliconLogo
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
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SiliconLogo(
                themeMode = state.themeMode,
                dynamicColor = state.dynamicColor,
                size = 42.dp
            )
            Text(
                text = "Silicon",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            Surface(
                modifier = Modifier
                    .size(44.dp)
                    .bounceClick(onClick = onOpenSettings),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.Tune,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
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

        Text(
            text = "ORIENTATION",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 6.dp, top = 2.dp)
        )

        OrientationGrid(
            active = state.isActive,
            selected = state.orientation,
            onSelect = { mode ->
                if (accessibilityEnabled) onSelectOrientation(mode) else onOpenSetup()
            }
        )

        SettingsGroupCard {
            SettingsToggleRow(
                icon = Icons.Rounded.Call,
                title = "Return on calls",
                subtitle = "Switch back to portrait when a call arrives",
                checked = state.returnOnCalls,
                onCheckedChange = onReturnOnCallsChange
            )
            SettingsGroupDivider()
            SettingsToggleRow(
                icon = Icons.AutoMirrored.Rounded.ExitToApp,
                title = "Stop when I leave the app",
                subtitle = "Disable once you switch away",
                checked = state.autoDisableOnLeaveApp,
                onCheckedChange = onAutoDisableChange
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun SetupPromptBanner(onOpenSetup: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick(onClick = onOpenSetup),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.VerifiedUser,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(22.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Finish setup",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = "Enable rotation service to unlock controls",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f)
                )
            }
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer
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
    val cardColor by animateColorAsState(
        targetValue = if (isActive) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        },
        animationSpec = tween(320),
        label = "hero_bg"
    )
    val cardBorderColor by animateColorAsState(
        targetValue = if (isActive) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
        } else {
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
        },
        animationSpec = tween(320),
        label = "hero_border"
    )
    val badgeBg by animateColorAsState(
        targetValue = if (isActive) {
            MaterialTheme.colorScheme.surfaceContainerLowest
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        },
        animationSpec = tween(320),
        label = "badge_bg"
    )
    val badgeIconTint by animateColorAsState(
        targetValue = if (isActive) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(320),
        label = "badge_tint"
    )
    val iconRotation by animateFloatAsState(
        targetValue = if (isActive) state.orientation.iconDegrees else 0f,
        animationSpec = spring(
            dampingRatio = 0.62f,
            stiffness = 280f
        ),
        label = "hero_rotation"
    )
    val titleColor = if (isActive) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val pulseTransition = rememberInfiniteTransition(label = "live_dot")
    val dotAlpha by pulseTransition.animateFloat(
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
        shape = RoundedCornerShape(32.dp),
        color = cardColor,
        border = BorderStroke(1.dp, cardBorderColor)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .alpha(if (isActive) dotAlpha else 0.4f)
                        .background(
                            if (isActive) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                )
                Text(
                    text = "ROTATION CONTROL",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(26.dp),
                    color = badgeBg,
                    border = BorderStroke(
                        1.dp,
                        if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
                        else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        ModernPhoneIcon(
                            tint = badgeIconTint,
                            rotationDegrees = iconRotation,
                            size = 42.dp,
                            filledScreen = isActive
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    AnimatedContent(
                        targetState = when {
                            !accessibilityEnabled -> "Setup needed"
                            isActive -> "Active"
                            else -> "Standby"
                        },
                        transitionSpec = {
                            (fadeIn(tween(220)) + scaleIn(initialScale = 0.94f)) togetherWith fadeOut(tween(140))
                        },
                        label = "status_title"
                    ) { statusText ->
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.displaySmall,
                            color = titleColor
                        )
                    }
                    AnimatedContent(
                        targetState = when {
                            !accessibilityEnabled -> "Accessibility service is off"
                            isActive -> "${state.orientation.label} locked"
                            else -> "Ready for ${state.defaultOrientation.label.lowercase()}"
                        },
                        transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(140)) },
                        label = "status_subtitle"
                    ) { subText ->
                        Text(
                            text = subText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = titleColor.copy(alpha = 0.78f)
                        )
                    }
                }
            }

            Button(
                onClick = onPrimaryAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.PowerSettingsNew,
                    contentDescription = null,
                    modifier = Modifier.size(19.dp)
                )
                Spacer(modifier = Modifier.size(10.dp))
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
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
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
        animationSpec = tween(260),
        label = "tile_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
        },
        animationSpec = tween(260),
        label = "tile_border"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(260),
        label = "tile_fg"
    )
    val cornerRadius by animateDpAsState(
        targetValue = if (selected) 26.dp else 20.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "tile_corner"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.08f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tile_icon_scale"
    )

    Surface(
        modifier = modifier
            .height(94.dp)
            .bounceClick(onClick = onClick),
        shape = RoundedCornerShape(cornerRadius),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ModernPhoneIcon(
                tint = contentColor,
                rotationDegrees = mode.iconDegrees,
                size = 28.dp,
                filledScreen = selected,
                modifier = Modifier.graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mode.label,
                style = MaterialTheme.typography.titleSmall,
                color = contentColor,
                maxLines = 1
            )
        }
    }
}
