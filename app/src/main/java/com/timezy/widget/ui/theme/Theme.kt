package com.timezy.widget.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

data class CountdownThemeColors(
    val background: Color,
    val card: Color,
    val surfaceVariant: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val accent: Color,
    val accentBackground: Color,
    val cardBorder: Color,
    val destructive: Color,
    val isDark: Boolean
)

val DarkCountdownThemeColors = CountdownThemeColors(
    background = DarkBackground,
    card = DarkCard,
    surfaceVariant = DarkSecondarySurface,
    border = DarkSubtleBorder,
    textPrimary = DarkPrimaryText,
    textSecondary = DarkSecondaryText,
    textMuted = DarkMutedText,
    accent = DarkPrimaryAccent,
    accentBackground = DarkAccentBackground,
    cardBorder = DarkCardBorder,
    destructive = DarkDestructive,
    isDark = true
)

val LightCountdownThemeColors = CountdownThemeColors(
    background = LightBackground,
    card = LightCard,
    surfaceVariant = LightSecondarySurface,
    border = LightSubtleBorder,
    textPrimary = LightPrimaryText,
    textSecondary = LightSecondaryText,
    textMuted = LightMutedText,
    accent = LightPrimaryAccent,
    accentBackground = LightAccentBackground,
    cardBorder = LightCardBorder,
    destructive = LightDestructive,
    isDark = false
)

val LocalCountdownColors = staticCompositionLocalOf { DarkCountdownThemeColors }

object CountdownTheme {
    val colors: CountdownThemeColors
        @Composable
        get() = LocalCountdownColors.current
}

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryAccent,
    onPrimary = DarkBackground,
    primaryContainer = DarkAccentBackground,
    onPrimaryContainer = DarkPrimaryAccent,
    secondary = Color(0xFF79D6B0),
    onSecondary = DarkBackground,
    secondaryContainer = DarkSecondarySurface,
    onSecondaryContainer = Color(0xFF79D6B0),
    tertiary = Color(0xFFFFA6B8),
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = DarkPrimaryText,
    surface = DarkCard,
    onSurface = DarkPrimaryText,
    surfaceVariant = DarkSecondarySurface,
    onSurfaceVariant = DarkSecondaryText,
    outline = DarkSubtleBorder,
    outlineVariant = DarkSubtleBorder,
    error = ErrorRed,
    onError = DarkPrimaryText
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimaryAccent,
    onPrimary = Color.White,
    primaryContainer = LightAccentBackground,
    onPrimaryContainer = LightPrimaryAccent,
    secondary = Color(0xFF059669),
    onSecondary = Color.White,
    secondaryContainer = LightSecondarySurface,
    onSecondaryContainer = LightPrimaryAccent,
    tertiary = Color(0xFFDB2777),
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightPrimaryText,
    surface = LightCard,
    onSurface = LightPrimaryText,
    surfaceVariant = LightSecondarySurface,
    onSurfaceVariant = LightSecondaryText,
    outline = LightSubtleBorder,
    outlineVariant = LightSubtleBorder,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun CountdownWidgetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val countdownColors = if (darkTheme) DarkCountdownThemeColors else LightCountdownThemeColors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = countdownColors.background.toArgb()
            window.navigationBarColor = countdownColors.card.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalCountdownColors provides countdownColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
