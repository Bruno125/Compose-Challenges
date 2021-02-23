package com.bruno.aybar.composechallenges.batman

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.bruno.aybar.composechallenges.R

private val batmanColors = darkColors(
    primary = Color(0xFFffe95e),
    surface = Color(0xFF120F0A),
)

private val batmanTypography = Typography(
    defaultFontFamily = FontFamily(Font(R.font.vidaloka_regular)),
    h1 = TextStyle(fontSize = 40.sp),
    h2 = TextStyle(fontSize = 20.sp),
    subtitle1 = TextStyle(fontSize = 12.sp, color = Color.Gray),
)

@Composable
fun BatmanTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = batmanColors,
        typography = batmanTypography,
        content = content
    )
}

@Composable
fun AnimatedBatmanPage() {
    val transitionState = remember { MutableTransitionState(BatmanPageState.LogoCovering) }

    val target = when (transitionState.currentState) {
        BatmanPageState.LogoCovering -> BatmanPageState.LogoCentered
        BatmanPageState.LogoCentered -> BatmanPageState.LogoAndHint
        BatmanPageState.LogoAndHint -> BatmanPageState.Completed
        else -> null
    }
    target?.let { transitionState.targetState = it }

    BatmanPage(transitionState)
}

@Composable
private fun BatmanPage(transitionState: MutableTransitionState<BatmanPageState>) {
    Surface(Modifier.fillMaxSize()) {
        BoxWithConstraints {

            val properties = buildUiProperties(transitionState, maxHeight.value)

            val logoHeight = Dp(properties.batmanLogoHeight) * 2
            val logoWidth = logoHeight * 2

            BatmanContent(
                maxWidth = maxWidth,
                modifier = Modifier.align(Alignment.TopCenter),
                batmanSizeProgress = properties.batmanSizeProgress
            )
            BatmanLogo(Modifier
                .align(BiasAlignment(verticalBias = properties.batmanLogoVerticalBias, horizontalBias = 0f))
                .size(width = logoWidth, height = logoHeight)
            )
            BatmanWelcomeHint(Modifier
                .graphicsLayer(alpha = properties.welcomeAlpha)
                .align(BiasAlignment(verticalBias = 0.1f, horizontalBias =  0f))
            )
            BatmanPageButtons(Modifier
                .graphicsLayer(alpha = properties.batmanButtonsAlpha)
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
            )
        }
    }
}

enum class BatmanPageState {
    LogoCovering,
    LogoCentered,
    LogoAndHint,
    Completed
}

private data class BatmanUiProperties(
    val batmanLogoHeight: Float,
    val batmanLogoVerticalBias: Float,
    val welcomeAlpha: Float,
    val batmanSizeProgress: Float,
    val batmanButtonsAlpha: Float,
    val current: BatmanPageState
)

@Composable
private fun buildUiProperties(transitionState: MutableTransitionState<BatmanPageState>, maxHeight: Float): BatmanUiProperties {
    val transition = updateTransition(transitionState)
    val tweenWithSimpleDelay = tween<Float>(durationMillis = 1000, delayMillis = 500)
    val batmanLogoHeight: Float by transition.animateFloat( { tweenWithSimpleDelay }) {
        when(transition.currentState) {
            BatmanPageState.LogoCovering -> maxHeight
            else -> 40f
        }
    }
    val batmanLogoVerticalBias: Float by transition.animateFloat( { tweenWithSimpleDelay }) {
        when(transition.currentState) {
            BatmanPageState.LogoCovering, BatmanPageState.LogoCentered -> 0f
            BatmanPageState.LogoAndHint, BatmanPageState.Completed -> -0.3f
        }
    }
    val welcomeAlpha: Float by transition.animateFloat( { tween(durationMillis = 800, delayMillis = 700) }) {
        when(transition.currentState) {
            BatmanPageState.LogoCovering, BatmanPageState.LogoCentered -> 0f
            BatmanPageState.LogoAndHint, BatmanPageState.Completed -> 1f
        }
    }
    val batmanSizeProgress: Float by transition.animateFloat( { tweenWithSimpleDelay }) {
        when(transition.currentState) {
            BatmanPageState.Completed -> 3f
            else -> 1f
        }
    }
    val batmanButtonsAlpha: Float by transition.animateFloat( { tweenWithSimpleDelay }) {
        when(transition.currentState) {
            BatmanPageState.Completed -> 1f
            else -> 0f
        }
    }

    return BatmanUiProperties(
        batmanLogoHeight = batmanLogoHeight,
        batmanLogoVerticalBias = batmanLogoVerticalBias,
        welcomeAlpha = welcomeAlpha,
        batmanSizeProgress = batmanSizeProgress,
        batmanButtonsAlpha = batmanButtonsAlpha,
        current = transition.currentState
    )

}

@Preview
@Composable
private fun BatmanPagePreview() {
    BatmanTheme {
        AnimatedBatmanPage()
    }
}