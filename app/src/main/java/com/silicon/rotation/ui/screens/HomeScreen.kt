package com.silicon.rotation.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.silicon.rotation.data.OrientationMode
import com.silicon.rotation.data.RotationState
import com.silicon.rotation.ui.components.SettingsGroupCard
import com.silicon.rotation.ui.components.SettingsGroupDivider
import com.silicon.rotation.ui.components.SettingsToggleRow
import com.silicon.rotation.ui.components.SiliconLogo

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
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SiliconLogo(
                themeMode = state.themeMode,
                dynamicColor = state.dynamicColor,
                size = 44.dp
            )
            Text(
                text = "Silicon",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            FilledTonalIconButton(
                onClick = onOpenSettings,
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Tune,
                    contentDescription = "Settings"
                )
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
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
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

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun SetupPromptBanner(onOpenSetup: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onOpenSetup),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer
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
                modifier = Modifier.size(24.dp)
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
        label = "hero_bg"
    )
    val badgeBg by animateColorAsState(
        targetValue = if (isActive) {
            MaterialTheme.colorScheme.surfaceContainerLowest
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        },
        label = "badge_bg"
    )
    val badgeIconTint by animateColorAsState(
        targetValue = if (isActive) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "badge_tint"
    )
    val iconRotation by animateFloatAsState(
        targetValue = if (isActive) state.orientation.iconDegrees else 0f,
        label = "hero_rotation"
    )
    val titleColor = if (isActive) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(36.dp),
        color = cardColor
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "ROTATION CONTROL",
                style = MaterialTheme.typography.labelMedium,
                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp, bottomEnd = 30.dp, bottomStart = 12.dp))
                        .background(badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Smartphone,
                        contentDescription = null,
                        tint = badgeIconTint,
                        modifier = Modifier
                            .size(40.dp)
                            .rotate(iconRotation)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = when {
                            !accessibilityEnabled -> "Setup needed"
                            isActive -> "Active"
                            else -> "Off"
                        },
                        style = MaterialTheme.typography.displaySmall,
                        color = titleColor
                    )
                    Text(
                        text = when {
                            !accessibilityEnabled -> "Accessibility service is off"
                            isActive -> "${state.orientation.label} is locked"
                            else -> "Ready for ${state.defaultOrientation.label.lowercase()}"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = titleColor.copy(alpha = 0.8f)
                    )
                }
            }

            Button(
                onClick = onPrimaryAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.PowerSettingsNew,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
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
            MaterialTheme.colorScheme.surfaceContainerHigh
        },
        label = "tile_bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        label = "tile_fg"
    )
    val bottomStartRadius by animateDpAsState(
        targetValue = if (selected) 14.dp else 28.dp,
        label = "tile_corner"
    )
    val topRadius by animateDpAsState(
        targetValue = if (selected) 36.dp else 28.dp,
        label = "tile_top"
    )

    Surface(
        modifier = modifier
            .height(92.dp)
            .clip(
                RoundedCornerShape(
                    topStart = topRadius,
                    topEnd = topRadius,
                    bottomEnd = topRadius,
                    bottomStart = bottomStartRadius
                )
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(
            topStart = topRadius,
            topEnd = topRadius,
            bottomEnd = topRadius,
            bottomStart = bottomStartRadius
        ),
        color = bgColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.Smartphone,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier
                    .size(26.dp)
                    .rotate(mode.iconDegrees)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mode.label,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
                maxLines = 1
            )
        }
    }
}
