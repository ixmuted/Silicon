package com.silicon.rotation.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
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

class SiliconSquareWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        RotationController.initialize(context)
        provideContent {
            val state by RotationController.state.collectAsState()
            GlanceTheme {
                SquareWidgetContent(state)
            }
        }
    }

    @Composable
    private fun SquareWidgetContent(state: RotationState) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .cornerRadius(26.dp)
                .background(GlanceTheme.colors.widgetBackground)
                .padding(12.dp)
        ) {
            // Compact header with small colorful Silicon logo + status (taps open app)
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .clickable(actionStartActivity<MainActivity>()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_silicon_emblem),
                    contentDescription = "Silicon",
                    modifier = GlanceModifier.size(20.dp)
                )
                Spacer(modifier = GlanceModifier.width(7.dp))
                Text(
                    text = "Silicon",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
                Text(
                    text = if (state.isActive) state.orientation.shortLabel else "Off",
                    style = TextStyle(
                        color = if (state.isActive) GlanceTheme.colors.primary else GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1
                )
            }

            Spacer(modifier = GlanceModifier.height(10.dp))

            // 2x2 grid of the 4 orientation controls (no refresh button)
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .defaultWeight()
            ) {
                OrientationGridButton(
                    mode = OrientationMode.PORTRAIT,
                    iconRes = R.drawable.ic_widget_phone_portrait,
                    active = state.isActive && state.orientation == OrientationMode.PORTRAIT,
                    modifier = GlanceModifier.defaultWeight().fillMaxHeight()
                )
                Spacer(modifier = GlanceModifier.width(8.dp))
                OrientationGridButton(
                    mode = OrientationMode.LANDSCAPE,
                    iconRes = R.drawable.ic_widget_phone_landscape,
                    active = state.isActive && state.orientation == OrientationMode.LANDSCAPE,
                    modifier = GlanceModifier.defaultWeight().fillMaxHeight()
                )
            }

            Spacer(modifier = GlanceModifier.height(8.dp))

            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .defaultWeight()
            ) {
                OrientationGridButton(
                    mode = OrientationMode.REVERSE_PORTRAIT,
                    iconRes = R.drawable.ic_widget_phone_rev_portrait,
                    active = state.isActive && state.orientation == OrientationMode.REVERSE_PORTRAIT,
                    modifier = GlanceModifier.defaultWeight().fillMaxHeight()
                )
                Spacer(modifier = GlanceModifier.width(8.dp))
                OrientationGridButton(
                    mode = OrientationMode.REVERSE_LANDSCAPE,
                    iconRes = R.drawable.ic_widget_phone_rev_landscape,
                    active = state.isActive && state.orientation == OrientationMode.REVERSE_LANDSCAPE,
                    modifier = GlanceModifier.defaultWeight().fillMaxHeight()
                )
            }
        }
    }

    @Composable
    private fun OrientationGridButton(
        mode: OrientationMode,
        iconRes: Int,
        active: Boolean,
        modifier: GlanceModifier = GlanceModifier
    ) {
        val bg = if (active) GlanceTheme.colors.primary else GlanceTheme.colors.surfaceVariant
        val fg = if (active) GlanceTheme.colors.onPrimary else GlanceTheme.colors.onSurfaceVariant
        Box(
            modifier = modifier
                .cornerRadius(16.dp)
                .background(bg)
                .clickable(
                    actionRunCallback<SelectOrientationAction>(
                        actionParametersOf(OrientationParamKey to mode.id)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                provider = ImageProvider(iconRes),
                contentDescription = mode.label,
                modifier = GlanceModifier.size(22.dp),
                colorFilter = ColorFilter.tint(fg)
            )
        }
    }
}
