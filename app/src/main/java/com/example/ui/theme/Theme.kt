package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SpotifyGreen,
    onPrimary = SpotifyBlack,
    primaryContainer = SpotifyGreenContainerDark,
    onPrimaryContainer = SpotifyGreenBright,
    secondary = SpotifyGreenBright,
    onSecondary = SpotifyBlack,
    secondaryContainer = Color(0xFF1B3D25),
    onSecondaryContainer = SpotifyGreenMint,
    tertiary = SpotifyGreenMint,
    onTertiary = SpotifyBlack,
    background = SpotifyBlack,
    onBackground = SpotifyTextPrimaryDark,
    surface = SpotifyDarkSurface,
    onSurface = SpotifyTextPrimaryDark,
    surfaceVariant = SpotifyDarkSurfaceVariant,
    onSurfaceVariant = SpotifyTextSecondaryDark,
    outline = SpotifyBorderDark,
    outlineVariant = Color(0xFF242424)
)

private val LightColorScheme = lightColorScheme(
    primary = SpotifyGreen,
    onPrimary = PureWhite,
    primaryContainer = SpotifyGreenContainerLight,
    onPrimaryContainer = SpotifyGreenDark,
    secondary = SpotifyGreenDark,
    onSecondary = PureWhite,
    secondaryContainer = SpotifyGreenSoft,
    onSecondaryContainer = Color(0xFF0F4722),
    tertiary = SpotifyGreenMint,
    onTertiary = PureWhite,
    background = SpotifyLightBg,
    onBackground = SpotifyTextPrimaryLight,
    surface = PureWhite,
    onSurface = SpotifyTextPrimaryLight,
    surfaceVariant = SpotifyLightSurfaceVariant,
    onSurfaceVariant = SpotifyTextSecondaryLight,
    outline = SpotifyBorderLight,
    outlineVariant = Color(0xFFD1D5DB)
)

@Composable
fun WaterReminderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backwards compatibility alias for default template
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    WaterReminderTheme(darkTheme = darkTheme, content = content)
}
