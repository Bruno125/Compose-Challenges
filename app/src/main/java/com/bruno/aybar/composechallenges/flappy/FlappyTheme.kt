package com.bruno.aybar.composechallenges.flappy

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bruno.aybar.composechallenges.ui.shapes

@Composable
fun FlappyBirdTheme(content: @Composable() () -> Unit) {
    val colors = lightColors(
        primary = lightGreen,
        primaryVariant = darkGreen,
        secondary = lightGreen,
        surface = skyColor,
        background = brown,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color.White,
        onSurface = Color.White
    )

    val typography = Typography(
        subtitle1 = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
        body1 = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        ),
    )

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}