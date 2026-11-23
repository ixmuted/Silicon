package com.silicon.rotation.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccessibilityNew
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DashboardCustomize
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.silicon.rotation.data.RotationState
import com.silicon.rotation.ui.components.SettingsGroupCard
import com.silicon.rotation.ui.components.SettingsGroupDivider
import com.silicon.rotation.ui.components.SiliconLogo

@Composable
fun SetupScreen(
    state: RotationState,
    accessibilityEnabled: Boolean,
    phonePermissionGranted: Boolean,
    notificationPermissionGranted: Boolean,
    tileAdded: Boolean,
    onBack: () -> Unit,
    onEnableAccessibility: () -> Unit,
    onAddQuickSettingsTile: () -> Unit,
    onRequestPhonePermission: () -> Unit,
    onRequestNotificationPermission: () -> Unit
) {
    val completedSteps = listOf(
        accessibilityEnabled,
        tileAdded,
        phonePermissionGranted,
        notificationPermissionGranted
    ).count { it }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "Setup",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        SiliconLogo(
                            themeMode = state.themeMode,
                            dynamicColor = state.dynamicColor,
                            size = 48.dp
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "System integration",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$completedSteps of 4 configured",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    LinearProgressIndicator(
                        progress = { completedSteps / 4f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        strokeCap = StrokeCap.Round,
                        trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                }
            }

            Text(
                text = "REQUIRED",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp)
            )

            SettingsGroupCard {
                SetupStepRow(
                    icon = Icons.Rounded.AccessibilityNew,
                    title = "Rotation service",
                    subtitle = "Allows Silicon to rotate apps and detect app switches",
                    completed = accessibilityEnabled,
                    actionLabel = "Enable",
                    onAction = onEnableAccessibility
                )
            }

            Text(
                text = "OPTIONAL",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp)
            )

            SettingsGroupCard {
                SetupStepRow(
                    icon = Icons.Rounded.DashboardCustomize,
                    title = "Quick Settings tile",
                    subtitle = "Toggle orientation directly from the notification panel",
                    completed = tileAdded,
                    actionLabel = "Add",
                    onAction = onAddQuickSettingsTile
                )
                SettingsGroupDivider()
                SetupStepRow(
                    icon = Icons.Rounded.Call,
                    title = "Call detection",
                    subtitle = "Automatically return to portrait for incoming calls",
                    completed = phonePermissionGranted,
                    actionLabel = "Allow",
                    onAction = onRequestPhonePermission
                )
                SettingsGroupDivider()
                SetupStepRow(
                    icon = Icons.Rounded.Notifications,
                    title = "Status notification",
                    subtitle = "Show quick controls while rotation is active",
                    completed = notificationPermissionGranted,
                    actionLabel = "Allow",
                    onAction = onRequestNotificationPermission
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Button(
                onClick = onBack,
                enabled = accessibilityEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = if (accessibilityEnabled) "Done" else "Enable rotation service to continue",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun SetupStepRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    completed: Boolean,
    actionLabel: String,
    onAction: () -> Unit
) {
    val iconContainer by animateColorAsState(
        targetValue = if (completed) {
            MaterialTheme.colorScheme.tertiaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        },
        label = "step_icon_bg"
    )
    val iconTint by animateColorAsState(
        targetValue = if (completed) {
            MaterialTheme.colorScheme.onTertiaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "step_icon_fg"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(iconContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (completed) Icons.Rounded.Check else icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (completed) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Text(
                    text = "Ready",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        } else {
            FilledTonalButton(
                onClick = onAction,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
