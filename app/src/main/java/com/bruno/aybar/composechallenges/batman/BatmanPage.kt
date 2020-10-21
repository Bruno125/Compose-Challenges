package com.bruno.aybar.composechallenges.batman

import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onActive
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.drawLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.font
import androidx.compose.ui.text.font.fontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import com.bruno.aybar.composechallenges.R
import com.bruno.aybar.composechallenges.common.AbsoluteAlignment
import com.bruno.aybar.composechallenges.common.AnimationSequenceStateHolder
import com.bruno.aybar.composechallenges.common.transition

private val batmanColors = darkColors(
    primary = Color(0xFFffe95e),
    surface = Color(0xFF120F0A),
)

private val batmanTypography = Typography(
    defaultFontFamily = fontFamily(font(R.font.vidaloka_regular)),
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
fun BatmanPage() {
    Surface(Modifier.fillMaxSize()) {
        WithConstraints {
            Box {
                val animationStateHolder = remember {
                    AnimationSequenceStateHolder(
                        listOf(
                            BatmanPageState.LogoCovering,
                            BatmanPageState.LogoCentered,
                            BatmanPageState.LogoAndHint,
                            BatmanPageState.Completed,
                        )
                    )
                }

                val transition = transition(
                    definition = remember { pageTransition(maxHeight.value) },
                    stateHolder = animationStateHolder
                )

                onActive {
                    animationStateHolder.start()
                }

                val logoHeight = Dp(transition[batmanLogoHeight]) * 2
                val logoWidth = logoHeight * 2

                BatmanContent(
                    maxWidth = maxWidth,
                    modifier = Modifier.align(Alignment.TopCenter),
                    batmanSizeProgress = transition[batmanSizeProgress]
                )
                BatmanLogo(Modifier
                    .align(AbsoluteAlignment(transition[batmanLogoVerticalBias]))
                    .size(width = logoWidth, height = logoHeight)
                )
                BatmanWelcomeHint(Modifier
                    .drawLayer(alpha = transition[welcomeAlpha])
                    .align(AbsoluteAlignment(0.1f))
                )
                BatmanPageButtons(Modifier
                    .drawLayer(alpha = transition[batmanButtonsAlpha])
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
                )
            }
        }
    }
}

enum class BatmanPageState {
    LogoCovering,
    LogoCentered,
    LogoAndHint,
    Completed
}

private val batmanLogoHeight = FloatPropKey()
private val batmanLogoVerticalBias = FloatPropKey()
private val welcomeAlpha = FloatPropKey()
private val batmanSizeProgress = FloatPropKey()
private val batmanButtonsAlpha = FloatPropKey()

private fun pageTransition(maxHeight: Float) = transitionDefinition<BatmanPageState> {
    state(BatmanPageState.LogoCovering) {
        this[batmanLogoHeight] = maxHeight
        this[batmanLogoVerticalBias] = 0f
        this[welcomeAlpha] = 0f
        this[batmanSizeProgress] = 3f
        this[batmanButtonsAlpha] = 0f
    }
    state(BatmanPageState.LogoCentered) {
        this[batmanLogoHeight] = 40f
        this[batmanLogoVerticalBias] = 0f
        this[welcomeAlpha] = 0f
        this[batmanSizeProgress] = 3f
        this[batmanButtonsAlpha] = 0f
    }
    state(BatmanPageState.LogoAndHint) {
        this[batmanLogoHeight] = 40f
        this[batmanLogoVerticalBias] = -0.3f
        this[welcomeAlpha] = 1f
        this[batmanSizeProgress] = 3f
        this[batmanButtonsAlpha] = 0f
    }
    state(BatmanPageState.Completed) {
        this[batmanLogoHeight] = 40f
        this[batmanLogoVerticalBias] = -0.3f
        this[welcomeAlpha] = 1f
        this[batmanSizeProgress] = 1f
        this[batmanButtonsAlpha] = 1f
    }
    transition(fromState = BatmanPageState.LogoCovering, toState = BatmanPageState.LogoCentered) {
        batmanLogoHeight using tween(durationMillis = 1000, delayMillis = 500)
    }
    transition(fromState = BatmanPageState.LogoCentered, toState = BatmanPageState.LogoAndHint) {
        batmanLogoVerticalBias using tween(durationMillis = 1000, delayMillis = 500)
        welcomeAlpha using tween(durationMillis = 800, delayMillis = 700)
    }
    transition(fromState = BatmanPageState.LogoAndHint, toState = BatmanPageState.Completed) {
        batmanSizeProgress using tween(durationMillis = 1000, delayMillis = 500)
        batmanButtonsAlpha using tween(durationMillis = 1000, delayMillis = 500)
    }


}

@Preview
@Composable
private fun BatmanPagePreview() {
    BatmanTheme {
        BatmanPage()
    }
}