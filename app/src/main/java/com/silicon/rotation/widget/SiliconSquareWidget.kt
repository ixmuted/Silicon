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
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.silicon.rotation.R
import com.silicon.rotation.data.RotationState
import com.silicon.rotation.service.RotationController

class SiliconSquareWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        RotationController.initialize(context)
        val state = RotationController.state.value
        provideContent {
            GlanceTheme {
                SquareWidgetContent(state)
            }
        }
    }

    @Composable
    private fun SquareWidgetContent(state: RotationState) {
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
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .cornerRadius(28.dp)
                .background(containerColor)
                .clickable(actionRunCallback<ToggleRotationAction>())
                .padding(14.dp)
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = GlanceModifier
                        .size(38.dp)
                        .cornerRadius(14.dp)
                        .background(
                            if (isActive) GlanceTheme.colors.primary else GlanceTheme.colors.secondaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_silicon_tile),
                        contentDescription = "Silicon",
                        modifier = GlanceModifier.size(20.dp),
                        colorFilter = ColorFilter.tint(
                            if (isActive) GlanceTheme.colors.onPrimary else GlanceTheme.colors.onSecondaryContainer
                        )
                    )
                }
                Spacer(modifier = GlanceModifier.defaultWeight())
                Box(
                    modifier = GlanceModifier
                        .size(34.dp)
                        .cornerRadius(17.dp)
                        .background(GlanceTheme.colors.surface)
                        .clickable(actionRunCallback<CycleOrientationAction>()),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_widget_rotate),
                        contentDescription = "Cycle orientation",
                        modifier = GlanceModifier.size(18.dp),
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface)
                    )
                }
            }
            Spacer(modifier = GlanceModifier.defaultWeight())
            Text(
                text = if (isActive) "Active" else "Off",
                style = TextStyle(
                    color = onContainerColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1
            )
            Spacer(modifier = GlanceModifier.height(2.dp))
            Text(
                text = if (isActive) state.orientation.shortLabel else "Tap to rotate",
                style = TextStyle(
                    color = onContainerColor,
                    fontSize = 12.sp
                ),
                maxLines = 1
            )
        }
    }
}
