package com.silicon.rotation.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.silicon.rotation.data.ThemeMode

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SiliconTheme(
    themeMode: ThemeMode,
    dynamicColor: Boolean,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val useDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.PURE_DARK -> true
    }
    val context = LocalContext.current
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !useDark
            insetsController.isAppearanceLightNavigationBars = !useDark
        }
    }
    val baseScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDark -> SiliconDarkColorScheme
        else -> SiliconLightColorScheme
    }
    val colorScheme = if (themeMode == ThemeMode.PURE_DARK) {
        baseScheme.copy(
            background = PureDarkBackground,
            surface = PureDarkBackground,
            surfaceContainerLowest = PureDarkBackground,
            surfaceContainerLow = Color(0xFF0E0E10),
            surfaceContainer = PureDarkCard,
            surfaceContainerHigh = PureDarkCardHigh,
            surfaceContainerHighest = PureDarkCardHighest,
            outlineVariant = PureDarkDivider
        )
    } else {
        baseScheme
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        motionScheme = MotionScheme.expressive(),
        typography = SiliconTypography,
        content = content
    )
}
