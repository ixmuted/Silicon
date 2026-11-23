package com.silicon.rotation.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.silicon.rotation.MainActivity
import com.silicon.rotation.R
import com.silicon.rotation.data.OrientationMode
import com.silicon.rotation.data.RotationState
import com.silicon.rotation.service.RotationController

class SiliconBarWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        RotationController.initialize(context)
        val state = RotationController.state.value
        provideContent {
            GlanceTheme {
                BarWidgetContent(state)
            }
        }
    }

    @Composable
    private fun BarWidgetContent(state: RotationState) {
        val isActive = state.isActive
        val containerColor = if (isActive) {
            GlanceTheme.colors.primaryContainer
        } else {
            GlanceTheme.colors.widgetBackground
        }
        val onContainerColor = if (isActive) {
            GlanceTheme.colors.onPrimaryContainer
        } else {
            GlanceTheme.colors.onSurface
        }
        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .cornerRadius(28.dp)
                .background(containerColor)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = GlanceModifier
                    .defaultWeight()
                    .clickable(actionStartActivity<MainActivity>()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = GlanceModifier
                        .size(40.dp)
                        .cornerRadius(16.dp)
                        .background(
                            if (isActive) GlanceTheme.colors.primary else GlanceTheme.colors.secondaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_silicon_tile),
                        contentDescription = "Silicon",
                        modifier = GlanceModifier.size(22.dp),
                        colorFilter = ColorFilter.tint(
                            if (isActive) GlanceTheme.colors.onPrimary else GlanceTheme.colors.onSecondaryContainer
                        )
                    )
                }
                Spacer(modifier = GlanceModifier.width(12.dp))
                Column {
                    Text(
                        text = if (isActive) state.orientation.shortLabel else "Silicon",
                        style = TextStyle(
                            color = onContainerColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = if (isActive) "Active" else "Off",
                        style = TextStyle(
                            color = onContainerColor,
                            fontSize = 12.sp
                        ),
                        maxLines = 1
                    )
                }
            }

            OrientationPillButton(
                iconRes = R.drawable.ic_widget_portrait,
                selected = isActive && state.orientation == OrientationMode.PORTRAIT,
                mode = OrientationMode.PORTRAIT
            )
            Spacer(modifier = GlanceModifier.width(6.dp))
            OrientationPillButton(
                iconRes = R.drawable.ic_widget_landscape,
                selected = isActive && state.orientation == OrientationMode.LANDSCAPE,
                mode = OrientationMode.LANDSCAPE
            )
            Spacer(modifier = GlanceModifier.width(6.dp))
            Box(
                modifier = GlanceModifier
                    .size(40.dp)
                    .cornerRadius(20.dp)
                    .background(
                        if (isActive) GlanceTheme.colors.primary else GlanceTheme.colors.surfaceVariant
                    )
                    .clickable(actionRunCallback<ToggleRotationAction>()),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_widget_power),
                    contentDescription = "Toggle rotation",
                    modifier = GlanceModifier.size(20.dp),
                    colorFilter = ColorFilter.tint(
                        if (isActive) GlanceTheme.colors.onPrimary else GlanceTheme.colors.onSurfaceVariant
                    )
                )
            }
        }
    }

    @Composable
    private fun OrientationPillButton(
        iconRes: Int,
        selected: Boolean,
        mode: OrientationMode
    ) {
        Box(
            modifier = GlanceModifier
                .size(40.dp)
                .cornerRadius(16.dp)
                .background(
                    if (selected) GlanceTheme.colors.primary else GlanceTheme.colors.surface
                )
                .clickable(
                    actionRunCallback<SetOrientationAction>(
                        actionParametersOf(OrientationParamKey to mode.id)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                provider = ImageProvider(iconRes),
                contentDescription = mode.label,
                modifier = GlanceModifier.size(20.dp),
                colorFilter = ColorFilter.tint(
                    if (selected) GlanceTheme.colors.onPrimary else GlanceTheme.colors.onSurface
                )
            )
        }
    }
}
