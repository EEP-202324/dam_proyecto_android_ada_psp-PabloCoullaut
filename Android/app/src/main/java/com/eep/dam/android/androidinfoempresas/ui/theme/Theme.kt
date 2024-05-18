package com.eep.dam.android.androidinfoempresas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = DarkBlue,
    primaryVariant = Purple700,
    secondary = Teal200,
    background = Black,
    surface = Black,
    onPrimary = White,
    onSecondary = Black,
    onBackground = White,
    onSurface = White
)

private val LightColorPalette = lightColors(
    primary = DarkBlue,
    primaryVariant = Purple700,
    secondary = Teal200,
    background = White,
    surface = White,
    onPrimary = White,
    onSecondary = Black,
    onBackground = Black,
    onSurface = Black
)

@Composable
fun AndroidInfoEmpresasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}