package com.silicon.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.rounded.AccessibilityNew
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DashboardCustomize
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.silicon.data.RotationState
import com.silicon.ui.components.ScreenHeader
import com.silicon.ui.components.SectionLabel
import com.silicon.ui.components.SettingsGroupCard
import com.silicon.ui.components.SettingsGroupDivider

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

    val animatedProgress by animateFloatAsState(
        targetValue = completedSteps / 4f,
        animationSpec = tween(400),
        label = "setup_progress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        ScreenHeader(
            title = "Set up Silicon",
            subtitle = "$completedSteps of 4 complete",
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outlineVariant,
                strokeCap = StrokeCap.Round,
                gapSize = 0.dp,
                drawStopIndicator = {}
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionLabel("Required")
                SettingsGroupCard {
                    SetupStepRow(
                        icon = Icons.Rounded.AccessibilityNew,
                        title = "Accessibility service",
                        subtitle = "Lets Silicon control orientation and detect app switches",
                        completed = accessibilityEnabled,
                        completedLabel = "Ready",
                        actionLabel = "Enable",
                        onAction = onEnableAccessibility
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionLabel("Optional")
                SettingsGroupCard {
                    SetupStepRow(
                        icon = Icons.Rounded.DashboardCustomize,
                        title = "Quick Settings tile",
                        subtitle = "Toggle from the notification shade",
                        completed = tileAdded,
                        completedLabel = "Added",
                        actionLabel = "Add",
                        onAction = onAddQuickSettingsTile
                    )
                    SettingsGroupDivider()
                    SetupStepRow(
                        icon = Icons.Rounded.Call,
                        title = "Call detection",
                        subtitle = "Return to portrait during calls",
                        completed = phonePermissionGranted,
                        completedLabel = "Ready",
                        actionLabel = "Allow",
                        onAction = onRequestPhonePermission
                    )
                    SettingsGroupDivider()
                    SetupStepRow(
                        icon = Icons.Rounded.Notifications,
                        title = "Status notification",
                        subtitle = "Quick controls while locked",
                        completed = notificationPermissionGranted,
                        completedLabel = "Ready",
                        actionLabel = "Allow",
                        onAction = onRequestNotificationPermission
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = onBack,
                enabled = accessibilityEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(
                    text = if (accessibilityEnabled) "Done" else "Enable the accessibility service to continue",
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
    completedLabel: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    val iconContainer by animateColorAsState(
        targetValue = if (completed) MaterialTheme.colorScheme.tertiaryContainer
        else MaterialTheme.colorScheme.surfaceContainerHigh,
        animationSpec = tween(200),
        label = "step_icon_bg"
    )
    val iconTint by animateColorAsState(
        targetValue = if (completed) MaterialTheme.colorScheme.tertiary
        else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(200),
        label = "step_icon_fg"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconContainer),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = completed,
                transitionSpec = { fadeIn(tween(160)) togetherWith fadeOut(tween(100)) },
                label = "step_icon_anim"
            ) { done ->
                Icon(
                    imageVector = if (done) Icons.Rounded.Check else icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        AnimatedContent(
            targetState = completed,
            transitionSpec = { fadeIn(tween(160)) togetherWith fadeOut(tween(100)) },
            label = "step_trailing_anim"
        ) { done ->
            if (done) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Text(
                        text = completedLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            } else {
                OutlinedButton(
                    onClick = onAction,
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(text = actionLabel, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
