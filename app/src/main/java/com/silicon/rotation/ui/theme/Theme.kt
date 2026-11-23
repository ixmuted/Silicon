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
        ThemeMode.DARK -> true
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

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDark) {
                // Keep Pure OLED Dark surfaces even when Material You dynamic accent is on
                dynamicDarkColorScheme(context).copy(
                    background = Color(0xFF000000),
                    surface = Color(0xFF000000),
                    surfaceContainerLowest = Color(0xFF000000),
                    surfaceContainerLow = Color(0xFF0A0A0D),
                    surfaceContainer = Color(0xFF111116),
                    surfaceContainerHigh = Color(0xFF1A1A22),
                    surfaceContainerHighest = Color(0xFF24242F),
                    outlineVariant = Color(0xFF22222B)
                )
            } else {
                // Keep crisp pure white canvas even when Material You dynamic accent is on
                dynamicLightColorScheme(context).copy(
                    background = Color(0xFFFFFFFF),
                    surface = Color(0xFFFFFFFF),
                    surfaceContainerLowest = Color(0xFFFFFFFF),
                    surfaceContainerLow = Color(0xFFFAFBFD),
                    surfaceContainer = Color(0xFFF6F8FB),
                    surfaceContainerHigh = Color(0xFFEDF1F7),
                    outlineVariant = Color(0xFFE5EAF2)
                )
            }
        }
        useDark -> SiliconDarkColorScheme
        else -> SiliconLightColorScheme
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        motionScheme = MotionScheme.expressive(),
        typography = SiliconTypography,
        content = content
    )
}
