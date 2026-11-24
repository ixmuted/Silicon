package com.silicon.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.silicon.data.OrientationMode
import com.silicon.data.RotationState
import com.silicon.ui.components.ModernPhoneIcon
import com.silicon.ui.components.SectionLabel
import com.silicon.ui.components.SettingsGroupCard
import com.silicon.ui.components.SettingsGroupDivider
import com.silicon.ui.components.SettingsToggleRow
import com.silicon.ui.components.SiliconLogo

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
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        val compact = maxHeight < 660.dp

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = maxHeight)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = if (compact) 8.dp else 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SiliconLogo(size = 30.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Silicon",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            HeroControlCard(
                state = state,
                accessibilityEnabled = accessibilityEnabled,
                compact = compact,
                onPrimaryAction = {
                    if (accessibilityEnabled) onToggleActive() else onOpenSetup()
                }
            )

            Column(verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 10.dp)) {
                SectionLabel("Orientation")
                OrientationGrid(
                    active = state.isActive,
                    selected = state.orientation,
                    tileHeight = if (compact) 48.dp else 60.dp,
                    onSelect = { mode ->
                        if (accessibilityEnabled) onSelectOrientation(mode) else onOpenSetup()
                    }
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 10.dp)) {
                SectionLabel("Automation")
                SettingsGroupCard {
                    SettingsToggleRow(
                        icon = Icons.Rounded.Call,
                        title = "Return on calls",
                        subtitle = "Switch to portrait when a call arrives",
                        checked = state.returnOnCalls,
                        compact = compact,
                        onCheckedChange = onReturnOnCallsChange
                    )
                    SettingsGroupDivider()
                    SettingsToggleRow(
                        icon = Icons.AutoMirrored.Rounded.ExitToApp,
                        title = "Stop on app switch",
                        subtitle = "Turn off when you leave the app",
                        checked = state.autoDisableOnLeaveApp,
                        compact = compact,
                        onCheckedChange = onAutoDisableChange
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroControlCard(
    state: RotationState,
    accessibilityEnabled: Boolean,
    compact: Boolean,
    onPrimaryAction: () -> Unit
) {
    val isActive = state.isActive && accessibilityEnabled
    val iconRotation by animateFloatAsState(
        targetValue = if (isActive) state.orientation.iconDegrees else 0f,
        animationSpec = tween(320, easing = FastOutSlowInEasing),
        label = "hero_rotation"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(
            modifier = Modifier.padding(if (compact) 18.dp else 22.dp),
            verticalArrangement = Arrangement.spacedBy(if (compact) 14.dp else 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusPill(
                    active = isActive,
                    label = when {
                        !accessibilityEnabled -> "Setup required"
                        isActive -> "Active"
                        else -> "Standby"
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
                ModernPhoneIcon(
                    tint = if (isActive) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    rotationDegrees = iconRotation,
                    size = if (compact) 32.dp else 36.dp,
                    filledScreen = isActive
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                AnimatedContent(
                    targetState = when {
                        !accessibilityEnabled -> "Almost there"
                        isActive -> state.orientation.label
                        else -> "Unlocked"
                    },
                    transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(120)) },
                    label = "hero_title"
                ) { label ->
                    Text(
                        text = label,
                        style = if (compact) MaterialTheme.typography.headlineMedium
                        else MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = when {
                        !accessibilityEnabled -> "Enable the accessibility service to get started."
                        isActive -> "Locked across all apps."
                        else -> "Default orientation: ${state.defaultOrientation.shortLabel}."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            val buttonModifier = Modifier
                .fillMaxWidth()
                .height(if (compact) 44.dp else 48.dp)
            val buttonShape = RoundedCornerShape(10.dp)
            if (isActive) {
                OutlinedButton(
                    onClick = onPrimaryAction,
                    modifier = buttonModifier,
                    shape = buttonShape,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text("Turn off", style = MaterialTheme.typography.labelLarge)
                }
            } else {
                Button(
                    onClick = onPrimaryAction,
                    modifier = buttonModifier,
                    shape = buttonShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = if (accessibilityEnabled) "Turn on" else "Open setup",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusPill(active: Boolean, label: String) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(
                        if (active) MaterialTheme.colorScheme.tertiary
                        else MaterialTheme.colorScheme.outline
                    )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun OrientationGrid(
    active: Boolean,
    selected: OrientationMode,
    tileHeight: Dp,
    onSelect: (OrientationMode) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OrientationMode.entries.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { mode ->
                    OrientationTile(
                        mode = mode,
                        selected = active && selected == mode,
                        onClick = { onSelect(mode) },
                        modifier = Modifier
                            .weight(1f)
                            .height(tileHeight)
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
    val container by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.surfaceContainerHighest
        else MaterialTheme.colorScheme.surfaceContainerLowest,
        animationSpec = tween(160),
        label = "tile_container"
    )
    val border by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outlineVariant,
        animationSpec = tween(160),
        label = "tile_border"
    )

    Surface(
        onClick = onClick,
        modifier = modifier.semantics {
            this.selected = selected
            role = Role.RadioButton
        },
        shape = RoundedCornerShape(12.dp),
        color = container,
        border = BorderStroke(if (selected) 1.5.dp else 1.dp, border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ModernPhoneIcon(
                tint = MaterialTheme.colorScheme.onSurface,
                rotationDegrees = mode.iconDegrees,
                size = 22.dp,
                filledScreen = selected
            )
            Text(
                text = mode.label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}
