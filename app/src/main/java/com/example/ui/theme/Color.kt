package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Light Mode Water-inspired Palette
val DeepBluePrimary = Color(0xFF0284C7)
val DeepBluePrimaryDark = Color(0xFF0369A1)
val BrightBlue = Color(0xFF0EA5E9)
val LightSkyBlue = Color(0xFF38BDF8)
val SoftIceBlue = Color(0xFFBAE6FD)
val PaleSkyBlue = Color(0xFFE0F2FE)
val UltraLightBlueBg = Color(0xFFF4F9FD)
val PureWhite = Color(0xFFFFFFFF)
val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF475569)
val TextTertiaryLight = Color(0xFF64748B)
val BorderLight = Color(0xFFE2E8F0)

// Dark Mode Palette (deep, comfortable, water-inspired, not pure black)
val DarkBackground = Color(0xFF0A121E)
val DarkSurface = Color(0xFF111D2E)
val DarkSurfaceVariant = Color(0xFF19283E)
val DarkPrimary = Color(0xFF38BDF8)
val DarkSecondary = Color(0xFF7DD3FC)
val DarkTertiary = Color(0xFF0EA5E9)
val TextPrimaryDark = Color(0xFFF1F5F9)
val TextSecondaryDark = Color(0xFF94A3B8)
val TextTertiaryDark = Color(0xFF64748B)
val BorderDark = Color(0xFF1E2E48)

// Water Gradients for specific visual elements (circle, primary buttons, headers, droplet)
val WaterProgressBrush = Brush.sweepGradient(
    listOf(
        Color(0xFF0284C7),
        Color(0xFF0EA5E9),
        Color(0xFF38BDF8),
        Color(0xFF67E8F9),
        Color(0xFF0284C7)
    )
)

val WaterButtonBrush = Brush.horizontalGradient(
    listOf(
        Color(0xFF0284C7),
        Color(0xFF0EA5E9)
    )
)

val WaterHeaderBrush = Brush.verticalGradient(
    listOf(
        Color(0xFFE0F2FE).copy(alpha = 0.6f),
        Color(0x00F4F9FD)
    )
)

val WaterDropBrush = Brush.verticalGradient(
    listOf(
        Color(0xFF38BDF8),
        Color(0xFF0284C7)
    )
)

val WaterWaveBrush = Brush.verticalGradient(
    listOf(
        Color(0xFF38BDF8).copy(alpha = 0.45f),
        Color(0xFF0284C7).copy(alpha = 0.85f)
    )
)
