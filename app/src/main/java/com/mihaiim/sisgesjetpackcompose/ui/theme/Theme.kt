package com.mihaiim.sisgesjetpackcompose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = Parsley,
    primaryVariant = Parsley,
    onPrimary = Color.White,
    secondary = Parsley,
    secondaryVariant = Parsley,
    onSecondary = Color.White,
)

private val LightColorPalette = lightColors(
    primary = Parsley,
    primaryVariant = Parsley,
    onPrimary = Color.White,
    secondary = Parsley,
    secondaryVariant = Parsley,
    onSecondary = Color.White,
)

@Composable
fun SisGesJetpackComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        color = Parsley,
    )

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}