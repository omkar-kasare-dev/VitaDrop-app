package com.social.vitadrop.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40


)

@Composable
fun VitaDropTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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

/*
package com.social.vitadrop.ui.theme // or com.social.vitadrop.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.social.vitadrop.presentation.theme.VitaDropColors

// Modern Light Color Scheme using VitaDropColors
private val VitaDropLightColorScheme = lightColorScheme(
    primary = VitaDropColors.PrimaryRose,
    onPrimary = VitaDropColors.SurfaceCard,
    primaryContainer = VitaDropColors.SoftRoseBg,
    onPrimaryContainer = VitaDropColors.DeepRose,
    secondary = VitaDropColors.FreshEmerald,
    onSecondary = VitaDropColors.SurfaceCard,
    secondaryContainer = VitaDropColors.FreshEmeraldBg,
    background = VitaDropColors.BgCanvas,
    onBackground = VitaDropColors.TextDark,
    surface = VitaDropColors.SurfaceCard,
    onSurface = VitaDropColors.TextDark,
    surfaceVariant = VitaDropColors.SoftRoseBg,
    onSurfaceVariant = VitaDropColors.TextMuted,
    outline = VitaDropColors.BorderLight
)

@Composable
fun VitaDropTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set dynamicColor to false to prioritize VitaDropColors over system wallpaper colors
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = VitaDropLightColorScheme // Extend to darkColorScheme if needed later

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
 */