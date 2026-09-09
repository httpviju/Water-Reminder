package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color(0xFF032840),
    primaryContainer = Color(0xFF03537E),
    onPrimaryContainer = Color(0xFFE0F2FE),
    secondary = DarkSecondary,
    onSecondary = Color(0xFF032840),
    secondaryContainer = Color(0xFF0B3B5C),
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = DarkTertiary,
    onTertiary = Color(0xFF032840),
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDark,
    outlineVariant = Color(0xFF253957)
)

private val LightColorScheme = lightColorScheme(
    primary = DeepBluePrimary,
    onPrimary = PureWhite,
    primaryContainer = PaleSkyBlue,
    onPrimaryContainer = DeepBluePrimaryDark,
    secondary = BrightBlue,
    onSecondary = PureWhite,
    secondaryContainer = SoftIceBlue,
    onSecondaryContainer = Color(0xFF025A88),
    tertiary = LightSkyBlue,
    onTertiary = PureWhite,
    background = UltraLightBlueBg,
    onBackground = TextPrimaryLight,
    surface = PureWhite,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFEFF6FC),
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight,
    outlineVariant = Color(0xFFCBD5E1)
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
