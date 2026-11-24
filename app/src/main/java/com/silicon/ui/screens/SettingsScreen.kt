package com.silicon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.AccessibilityNew
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CropSquare
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DashboardCustomize
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.ScreenRotation
import androidx.compose.material.icons.rounded.UnfoldMore
import androidx.compose.material.icons.rounded.ViewAgenda
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.silicon.data.OrientationMode
import com.silicon.data.RotationState
import com.silicon.data.ThemeMode
import com.silicon.ui.components.ScreenHeader
import com.silicon.ui.components.SectionLabel
import com.silicon.ui.components.SettingsGroupCard
import com.silicon.ui.components.SettingsGroupDivider
import com.silicon.ui.components.SettingsNavigationRow
import com.silicon.ui.components.SettingsToggleRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: RotationState,
    accessibilityEnabled: Boolean,
    tileAdded: Boolean,
    onBack: () -> Unit,
    onOpenSetup: () -> Unit,
    onAddQuickSettingsTile: () -> Unit,
    onPinBarWidget: () -> Unit,
    onPinSquareWidget: () -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    onDefaultOrientationChange: (OrientationMode) -> Unit,
    onReturnOnCallsChange: (Boolean) -> Unit,
    onAutoDisableChange: (Boolean) -> Unit,
    onShowNotificationChange: (Boolean) -> Unit
) {
    var showThemeSheet by remember { mutableStateOf(false) }
    var showOrientationSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        ScreenHeader(title = "Settings", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionLabel("General")
                SettingsGroupCard {
                    SettingsNavigationRow(
                        icon = Icons.Rounded.AccessibilityNew,
                        title = "Setup & permissions",
                        trailingText = if (accessibilityEnabled) "Ready" else "Action needed",
                        trailingIcon = Icons.Rounded.ChevronRight,
                        onClick = onOpenSetup
                    )
                    SettingsGroupDivider()
                    SettingsNavigationRow(
                        icon = Icons.Rounded.DashboardCustomize,
                        title = "Quick Settings tile",
                        trailingText = if (tileAdded) "Added" else null,
                        trailingIcon = Icons.Rounded.ChevronRight,
                        onClick = onAddQuickSettingsTile
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionLabel("Appearance")
                SettingsGroupCard {
                    SettingsNavigationRow(
                        icon = Icons.Rounded.DarkMode,
                        title = "Theme",
                        subtitle = "Light, dark, or match your device",
                        trailingText = state.themeMode.label,
                        trailingIcon = Icons.Rounded.UnfoldMore,
                        onClick = { showThemeSheet = true }
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionLabel("Behavior")
                SettingsGroupCard {
                    SettingsNavigationRow(
                        icon = Icons.Rounded.ScreenRotation,
                        title = "Default orientation",
                        trailingText = state.defaultOrientation.shortLabel,
                        trailingIcon = Icons.Rounded.UnfoldMore,
                        onClick = { showOrientationSheet = true }
                    )
                    SettingsGroupDivider()
                    SettingsToggleRow(
                        icon = Icons.Rounded.Call,
                        title = "Return on calls",
                        subtitle = "Switch to portrait when a call arrives",
                        checked = state.returnOnCalls,
                        onCheckedChange = onReturnOnCallsChange
                    )
                    SettingsGroupDivider()
                    SettingsToggleRow(
                        icon = Icons.AutoMirrored.Rounded.ExitToApp,
                        title = "Stop on app switch",
                        subtitle = "Turn off when you leave the app",
                        checked = state.autoDisableOnLeaveApp,
                        onCheckedChange = onAutoDisableChange
                    )
                    SettingsGroupDivider()
                    SettingsToggleRow(
                        icon = Icons.Rounded.Notifications,
                        title = "Active notification",
                        subtitle = "Quick controls while locked",
                        checked = state.showNotification,
                        onCheckedChange = onShowNotificationChange
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionLabel("Widgets")
                SettingsGroupCard {
                    SettingsNavigationRow(
                        icon = Icons.Rounded.ViewAgenda,
                        title = "Bar widget",
                        subtitle = "4×1 strip with all orientations",
                        trailingIcon = Icons.Rounded.ChevronRight,
                        onClick = onPinBarWidget
                    )
                    SettingsGroupDivider()
                    SettingsNavigationRow(
                        icon = Icons.Rounded.CropSquare,
                        title = "Grid widget",
                        subtitle = "2×2 pad with all orientations",
                        trailingIcon = Icons.Rounded.ChevronRight,
                        onClick = onPinSquareWidget
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showThemeSheet) {
        SelectionSheet(
            title = null,
            onDismiss = { showThemeSheet = false }
        ) {
            ThemeMode.entries.forEach { mode ->
                SelectionSheetRow(
                    label = mode.label,
                    selected = state.themeMode == mode,
                    onClick = {
                        onThemeModeChange(mode)
                        showThemeSheet = false
                    }
                )
            }
        }
    }

    if (showOrientationSheet) {
        SelectionSheet(
            title = "Default orientation",
            onDismiss = { showOrientationSheet = false }
        ) {
            OrientationMode.entries.forEach { mode ->
                SelectionSheetRow(
                    label = mode.label,
                    selected = state.defaultOrientation == mode,
                    onClick = {
                        onDefaultOrientationChange(mode)
                        showOrientationSheet = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectionSheet(
    title: String?,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }
            content()
        }
    }
}

@Composable
private fun SelectionSheetRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = if (selected) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            if (selected) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
