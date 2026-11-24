package com.silicon.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.silicon.R

val SiliconDisplayFont = FontFamily(
    Font(R.font.newsreader_regular, FontWeight.Normal),
    Font(R.font.newsreader_medium, FontWeight.Medium)
)

val SiliconBodyFont = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold)
)

private fun display(size: Int, line: Int, tracking: Double) = TextStyle(
    fontFamily = SiliconDisplayFont,
    fontWeight = FontWeight.Normal,
    fontSize = size.sp,
    lineHeight = line.sp,
    letterSpacing = tracking.sp
)

private fun sans(size: Double, line: Double, weight: FontWeight, tracking: Double = 0.0) = TextStyle(
    fontFamily = SiliconBodyFont,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = line.sp,
    letterSpacing = tracking.sp
)

val SiliconTypography = Typography(
    displayLarge = display(48, 52, -1.2),
    displayMedium = display(40, 44, -0.9),
    displaySmall = display(34, 38, -0.7),
    headlineLarge = display(32, 38, -0.6),
    headlineMedium = display(28, 34, -0.5),
    headlineSmall = display(24, 30, -0.4),
    titleLarge = sans(20.0, 26.0, FontWeight.Medium),
    titleMedium = sans(16.0, 22.0, FontWeight.Medium),
    titleSmall = sans(14.0, 20.0, FontWeight.Medium),
    bodyLarge = sans(16.0, 24.0, FontWeight.Normal),
    bodyMedium = sans(14.0, 21.0, FontWeight.Normal),
    bodySmall = sans(13.0, 19.0, FontWeight.Normal),
    labelLarge = sans(15.0, 20.0, FontWeight.Medium),
    labelMedium = sans(13.0, 18.0, FontWeight.Medium),
    labelSmall = sans(12.0, 16.0, FontWeight.Medium, 0.2)
)
