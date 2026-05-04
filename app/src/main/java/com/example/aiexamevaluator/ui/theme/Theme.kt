package com.example.aiexamevaluator.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GreenTertiary,
    onPrimary = DarkBackground,
    primaryContainer = GreenPrimaryDark,
    onPrimaryContainer = DarkOnSurface,
    secondary = GreenSecondary,
    onSecondary = DarkBackground,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = DarkOnSurface,
    tertiary = GreenPrimary,
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutline,
    surfaceTint = GreenTertiary
)

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = GreenSurfaceVariant,
    onPrimaryContainer = GreenPrimaryDark,
    secondary = GreenSecondary,
    onSecondary = Color.White,
    secondaryContainer = GreenCardTint,
    onSecondaryContainer = GreenPrimaryDark,
    tertiary = GreenTertiary,
    onTertiary = Color.White,
    background = GreenBackground,
    onBackground = GreenOnSurface,
    surface = GreenSurface,
    onSurface = GreenOnSurface,
    surfaceVariant = GreenSurfaceVariant,
    onSurfaceVariant = GreenOnSurfaceVariant,
    outline = GreenOutline,
    outlineVariant = GreenOutline,
    surfaceTint = GreenPrimary
)

@Composable
fun AIExamEvaluatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}