package com.silicon.rotation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.silicon.rotation.R
import com.silicon.rotation.data.ThemeMode

@Composable
fun SiliconLogo(
    themeMode: ThemeMode,
    dynamicColor: Boolean,
    size: Dp = 44.dp,
    modifier: Modifier = Modifier
) {
    val containerColor = when {
        themeMode == ThemeMode.PURE_DARK -> Color(0xFF141416)
        dynamicColor -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceContainerHigh
    }
    val tint = when {
        themeMode == ThemeMode.PURE_DARK -> Color.White
        dynamicColor -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> null
    }
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.32f))
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        if (tint != null) {
            Image(
                painter = painterResource(id = R.drawable.ic_silicon_tile),
                contentDescription = "Silicon logo",
                modifier = Modifier.size(size * 0.62f),
                colorFilter = ColorFilter.tint(tint)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_silicon_emblem),
                contentDescription = "Silicon logo",
                modifier = Modifier.size(size * 0.72f)
            )
        }
    }
}
