package com.silicon.rotation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.silicon.rotation.R
import com.silicon.rotation.data.ThemeMode

@Composable
fun SiliconLogo(
    themeMode: ThemeMode,
    @Suppress("UNUSED_PARAMETER") dynamicColor: Boolean = true,
    size: Dp = 44.dp,
    modifier: Modifier = Modifier
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val containerColor = if (isDark) Color(0xFF14151D) else Color(0xFFFFFFFF)
    val borderColor = if (isDark) Color(0xFF272936) else Color(0xFFE5EAF2)

    // Always render the colorful emblem in both Light and Dark themes
    Surface(
        modifier = modifier.size(size),
        shape = RoundedCornerShape(size * 0.30f),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = if (isDark) 0.dp else 1.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_silicon_emblem),
                contentDescription = "Silicon logo",
                modifier = Modifier.size(size * 0.72f)
            )
        }
    }
}
