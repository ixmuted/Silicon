package com.silicon.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Modern 2026 bezel-less smartphone silhouette with Dynamic Island pill at the top
 * and home indicator bar at the bottom so orientation direction is unmistakable.
 */
@Composable
fun ModernPhoneIcon(
    tint: Color,
    rotationDegrees: Float = 0f,
    size: Dp = 28.dp,
    filledScreen: Boolean = false,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .size(size)
            .graphicsLayer { rotationZ = rotationDegrees }
    ) {
        val w = this.size.width
        val h = this.size.height
        val phoneW = w * 0.56f
        val phoneH = h * 0.86f
        val left = (w - phoneW) / 2f
        val top = (h - phoneH) / 2f
        val corner = phoneW * 0.24f
        val strokeW = (w * 0.075f).coerceAtLeast(1.8.dp.toPx())

        if (filledScreen) {
            drawRoundRect(
                color = tint.copy(alpha = 0.14f),
                topLeft = Offset(left, top),
                size = Size(phoneW, phoneH),
                cornerRadius = CornerRadius(corner, corner)
            )
        }

        // Sleek frameless chassis
        drawRoundRect(
            color = tint,
            topLeft = Offset(left, top),
            size = Size(phoneW, phoneH),
            cornerRadius = CornerRadius(corner, corner),
            style = Stroke(width = strokeW)
        )

        // Top Dynamic Island / pill camera cutout (marks the "top" of the screen)
        val islandW = phoneW * 0.36f
        val islandH = (h * 0.068f).coerceAtLeast(2.dp.toPx())
        drawRoundRect(
            color = tint,
            topLeft = Offset(left + (phoneW - islandW) / 2f, top + phoneH * 0.11f),
            size = Size(islandW, islandH),
            cornerRadius = CornerRadius(islandH / 2f, islandH / 2f)
        )

        // Subtle bottom gesture home bar
        val barW = phoneW * 0.34f
        val barY = top + phoneH * 0.88f
        drawLine(
            color = tint.copy(alpha = 0.7f),
            start = Offset(left + (phoneW - barW) / 2f, barY),
            end = Offset(left + (phoneW + barW) / 2f, barY),
            strokeWidth = (strokeW * 0.72f).coerceAtLeast(1.4.dp.toPx()),
            cap = StrokeCap.Round
        )
    }
}
