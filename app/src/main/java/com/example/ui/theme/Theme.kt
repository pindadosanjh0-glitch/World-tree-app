package com.example.ui.theme

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
    primary = Green80,
    onPrimary = DarkForest,
    primaryContainer = ForestGreen,
    onPrimaryContainer = SoftMint,
    secondary = Brown80,
    onSecondary = DarkEarth,
    secondaryContainer = EarthBrown,
    onSecondaryContainer = WarmSand,
    tertiary = Sage80,
    onTertiary = DarkSage,
    tertiaryContainer = SageGreen,
    onTertiaryContainer = SoftSage,
    background = EcoBackgroundDark,
    onBackground = Color(0xFFE2E9E3),
    surface = EcoSurfaceDark,
    onSurface = Color(0xFFE2E9E3),
    surfaceVariant = EcoSurfaceVariantDark,
    onSurfaceVariant = Color(0xFFC1CDC3)
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = Color.White,
    primaryContainer = SoftMint,
    onPrimaryContainer = DarkForest,
    secondary = EarthBrown,
    onSecondary = Color.White,
    secondaryContainer = WarmSand,
    onSecondaryContainer = DarkEarth,
    tertiary = SageGreen,
    onTertiary = Color.White,
    tertiaryContainer = SoftSage,
    onTertiaryContainer = DarkSage,
    background = EcoBackgroundLight,
    onBackground = Color(0xFF1D221E),
    surface = EcoSurfaceLight,
    onSurface = Color(0xFF1D221E),
    surfaceVariant = EcoSurfaceVariantLight,
    onSurfaceVariant = Color(0xFF424E45)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set dynamicColor default to false so our brand's forest green & earthy palette is showcased
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
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
