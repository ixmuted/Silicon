package com.silicon.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object SiliconPalette {
    val Coral = Color(0xFFCC785C)
    val CoralPressed = Color(0xFFA9583E)
    val CoralOnDark = Color(0xFFD97757)

    val Canvas = Color(0xFFFAF9F5)
    val SurfaceSoft = Color(0xFFF5F0E8)
    val SurfaceCard = Color(0xFFEFE9DE)
    val SurfaceCreamStrong = Color(0xFFE8E0D2)
    val Hairline = Color(0xFFE6DFD8)
    val HairlineStrong = Color(0xFFD6CEC1)

    val Ink = Color(0xFF141413)
    val Muted = Color(0xFF6C6A64)

    val Dark = Color(0xFF181715)
    val DarkSoft = Color(0xFF1F1E1B)
    val DarkElevated = Color(0xFF252320)
    val DarkStrong = Color(0xFF302E2A)
    val DarkHairline = Color(0xFF34322D)
    val DarkHairlineStrong = Color(0xFF4A4740)
    val OnDark = Color(0xFFFAF9F5)
    val OnDarkSoft = Color(0xFFA09D96)

    val Success = Color(0xFF5DB872)
}

val SiliconLightColorScheme = lightColorScheme(
    primary = SiliconPalette.Coral,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFF6E5DC),
    onPrimaryContainer = SiliconPalette.CoralPressed,
    secondary = SiliconPalette.Ink,
    onSecondary = SiliconPalette.Canvas,
    secondaryContainer = SiliconPalette.SurfaceCard,
    onSecondaryContainer = SiliconPalette.Ink,
    tertiary = Color(0xFF2F8F4E),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE4F0E6),
    onTertiaryContainer = Color(0xFF24663A),
    background = SiliconPalette.Canvas,
    onBackground = SiliconPalette.Ink,
    surface = SiliconPalette.Canvas,
    onSurface = SiliconPalette.Ink,
    surfaceVariant = SiliconPalette.SurfaceCard,
    onSurfaceVariant = SiliconPalette.Muted,
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = SiliconPalette.Canvas,
    surfaceContainer = SiliconPalette.SurfaceSoft,
    surfaceContainerHigh = SiliconPalette.SurfaceCard,
    surfaceContainerHighest = SiliconPalette.SurfaceCreamStrong,
    outline = SiliconPalette.HairlineStrong,
    outlineVariant = SiliconPalette.Hairline,
    error = Color(0xFFC64545),
    onError = Color(0xFFFFFFFF)
)

val SiliconDarkColorScheme = darkColorScheme(
    primary = SiliconPalette.CoralOnDark,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF3A2820),
    onPrimaryContainer = Color(0xFFF2C9B8),
    secondary = SiliconPalette.OnDark,
    onSecondary = SiliconPalette.Dark,
    secondaryContainer = SiliconPalette.DarkStrong,
    onSecondaryContainer = SiliconPalette.OnDark,
    tertiary = SiliconPalette.Success,
    onTertiary = SiliconPalette.Dark,
    tertiaryContainer = Color(0xFF1E3326),
    onTertiaryContainer = Color(0xFFA8DDB4),
    background = SiliconPalette.Dark,
    onBackground = SiliconPalette.OnDark,
    surface = SiliconPalette.Dark,
    onSurface = SiliconPalette.OnDark,
    surfaceVariant = SiliconPalette.DarkElevated,
    onSurfaceVariant = SiliconPalette.OnDarkSoft,
    surfaceContainerLowest = SiliconPalette.DarkSoft,
    surfaceContainerLow = SiliconPalette.DarkSoft,
    surfaceContainer = SiliconPalette.DarkSoft,
    surfaceContainerHigh = SiliconPalette.DarkElevated,
    surfaceContainerHighest = SiliconPalette.DarkStrong,
    outline = SiliconPalette.DarkHairlineStrong,
    outlineVariant = SiliconPalette.DarkHairline,
    error = Color(0xFFE57373),
    onError = SiliconPalette.Dark
)
