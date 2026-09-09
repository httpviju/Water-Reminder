package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Spotify Iconic Palette
val SpotifyGreen = Color(0xFF1DB954)
val SpotifyGreenBright = Color(0xFF1ED760)
val SpotifyGreenDark = Color(0xFF169C46)
val SpotifyGreenMint = Color(0xFF57E389)
val SpotifyGreenSoft = Color(0xFF86EFAC)
val SpotifyGreenContainerDark = Color(0xFF0F381E)
val SpotifyGreenContainerLight = Color(0xFFDCFCE7)

// Spotify Dark Canvas (Iconic Spotify UI)
val SpotifyBlack = Color(0xFF121212)
val SpotifyDarkSurface = Color(0xFF181818)
val SpotifyDarkElevated = Color(0xFF242424)
val SpotifyDarkSurfaceVariant = Color(0xFF282828)
val SpotifyBorderDark = Color(0xFF333333)
val SpotifyTextPrimaryDark = Color(0xFFFFFFFF)
val SpotifyTextSecondaryDark = Color(0xFFB3B3B3)
val SpotifyTextTertiaryDark = Color(0xFF727272)

// Spotify Light Canvas
val SpotifyLightBg = Color(0xFFF9FAF9)
val SpotifyLightSurface = Color(0xFFFFFFFF)
val SpotifyLightSurfaceVariant = Color(0xFFF0FDF4)
val SpotifyTextPrimaryLight = Color(0xFF121212)
val SpotifyTextSecondaryLight = Color(0xFF4B5563)
val SpotifyBorderLight = Color(0xFFE5E7EB)

// Compatibility Aliases mapped to Spotify Theme
val DeepBluePrimary = SpotifyGreen
val DeepBluePrimaryDark = SpotifyGreenDark
val BrightBlue = SpotifyGreenBright
val LightSkyBlue = SpotifyGreenMint
val SoftIceBlue = SpotifyGreenSoft
val PaleSkyBlue = SpotifyGreenContainerLight
val UltraLightBlueBg = SpotifyLightBg
val PureWhite = Color(0xFFFFFFFF)
val TextPrimaryLight = SpotifyTextPrimaryLight
val TextSecondaryLight = SpotifyTextSecondaryLight
val TextTertiaryLight = Color(0xFF6B7280)
val BorderLight = SpotifyBorderLight

val DarkBackground = SpotifyBlack
val DarkSurface = SpotifyDarkSurface
val DarkSurfaceVariant = SpotifyDarkSurfaceVariant
val DarkPrimary = SpotifyGreenBright
val DarkSecondary = SpotifyGreen
val DarkTertiary = SpotifyGreenMint
val TextPrimaryDark = SpotifyTextPrimaryDark
val TextSecondaryDark = SpotifyTextSecondaryDark
val TextTertiaryDark = SpotifyTextTertiaryDark
val BorderDark = SpotifyBorderDark

// Spotify Water Gradients
val WaterProgressBrush = Brush.sweepGradient(
    listOf(
        SpotifyGreen,
        SpotifyGreenBright,
        SpotifyGreenMint,
        Color(0xFF22C55E),
        SpotifyGreen
    )
)

val WaterButtonBrush = Brush.horizontalGradient(
    listOf(
        SpotifyGreen,
        SpotifyGreenBright
    )
)

val WaterHeaderBrush = Brush.verticalGradient(
    listOf(
        SpotifyGreen.copy(alpha = 0.25f),
        Color.Transparent
    )
)

val WaterDropBrush = Brush.verticalGradient(
    listOf(
        SpotifyGreenBright,
        SpotifyGreenDark
    )
)

val WaterWaveBrush = Brush.verticalGradient(
    listOf(
        SpotifyGreenBright.copy(alpha = 0.55f),
        SpotifyGreen.copy(alpha = 0.85f)
    )
)

