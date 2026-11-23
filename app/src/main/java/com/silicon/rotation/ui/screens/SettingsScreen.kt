package com.silicon.rotation.ui.screens

import android.os.Build
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.AccessibilityNew
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CropSquare
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DashboardCustomize
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.ScreenRotation
import androidx.compose.material.icons.rounded.UnfoldMore
import androidx.compose.material.icons.rounded.ViewAgenda
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.silicon.rotation.data.OrientationMode
import com.silicon.rotation.data.RotationState
import com.silicon.rotation.data.ThemeMode
import com.silicon.rotation.ui.components.SettingsGroupCard
import com.silicon.rotation.ui.components.SettingsGroupDivider
import com.silicon.rotation.ui.components.SettingsNavigationRow
import com.silicon.rotation.ui.components.SettingsToggleRow

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
    onDynamicColorChange: (Boolean) -> Unit,
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
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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

            Text(
                text = "PREFERENCES",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 6.dp, top = 4.dp)
            )

            SettingsGroupCard {
                SettingsNavigationRow(
                    icon = Icons.Rounded.DarkMode,
                    title = "Theme",
                    subtitle = "Pure OLED dark, crisp white, or system",
                    trailingText = state.themeMode.label,
                    trailingIcon = Icons.Rounded.UnfoldMore,
                    onClick = { showThemeSheet = true }
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    SettingsGroupDivider()
                    SettingsToggleRow(
                        icon = Icons.Rounded.Palette,
                        title = "Dynamic color",
                        subtitle = "Match wallpaper accent palette",
                        checked = state.dynamicColor,
                        onCheckedChange = onDynamicColorChange
                    )
                }
                SettingsGroupDivider()
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
                    subtitle = "Portrait when a call arrives",
                    checked = state.returnOnCalls,
                    onCheckedChange = onReturnOnCallsChange
                )
                SettingsGroupDivider()
                SettingsToggleRow(
                    icon = Icons.AutoMirrored.Rounded.ExitToApp,
                    title = "Stop on app switch",
                    subtitle = "Disable when leaving the current app",
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

            Text(
                text = "WIDGETS",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 6.dp, top = 4.dp)
            )

            SettingsGroupCard {
                SettingsNavigationRow(
                    icon = Icons.Rounded.ViewAgenda,
                    title = "Bar widget",
                    subtitle = "4x1 strip with all 4 orientation buttons",
                    trailingIcon = Icons.Rounded.ChevronRight,
                    onClick = onPinBarWidget
                )
                SettingsGroupDivider()
                SettingsNavigationRow(
                    icon = Icons.Rounded.CropSquare,
                    title = "Grid widget",
                    subtitle = "2x2 compact pad with all 4 orientation buttons",
                    trailingIcon = Icons.Rounded.ChevronRight,
                    onClick = onPinSquareWidget
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    if (showThemeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showThemeSheet = false },
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 20.dp)
            ) {
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )
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
    }

    if (showOrientationSheet) {
        ModalBottomSheet(
            onDismissRequest = { showOrientationSheet = false },
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 20.dp)
            ) {
                Text(
                    text = "Default orientation",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )
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
}

@Composable
private fun SelectionSheetRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (selected) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
