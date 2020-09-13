package com.bruno.aybar.composechallenges

import androidx.compose.animation.ColorPropKey
import androidx.compose.animation.core.*
import androidx.compose.animation.transition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.bruno.aybar.composechallenges.ui.ComposeChallengesTheme

private val radiusState = FloatPropKey()
private val colorState = ColorPropKey()
private val positionState = FloatPropKey()

@Composable
fun CircleAnim(circleState: AnimState) {
    val state = transition(
        definition = testAnimation(),
        initState = circleState,
        toState = circleState.next()
    )

    Canvas(modifier = Modifier
        .height(100.dp)
        .fillMaxWidth()
    ) {
        val circleRadius = (size.height * state[radiusState]) / 2

        val centerY = center.y
        val offset = circleRadius * (1 - state[positionState] * 2)
        val centerX = (state[positionState] * size.width) + offset

        drawCircle(
            color = state[colorState],
            radius = circleRadius,
            center = Offset(centerX, centerY)
        )
    }
}

enum class AnimState {
    Small, Big;

    fun next() = when(this) {
        Small -> Big
        Big -> Small
    }
}

private fun testAnimation() = transitionDefinition<AnimState> {
    state(AnimState.Small) {
        this[radiusState] = 0.5f
        this[colorState] = Color.Red
        this[positionState] = 0f
    }
    state(AnimState.Big) {
        this[radiusState] = 1.0f
        this[colorState] = Color.Blue
        this[positionState] = 1f
    }

    transition(fromState = AnimState.Small, toState = AnimState.Big) {
        radiusState using tween(
            easing = FastOutSlowInEasing
        )
        colorState using tween(
            easing = FastOutSlowInEasing
        )
        positionState using tween(
            easing = FastOutSlowInEasing
        )
    }
    transition(fromState = AnimState.Big, toState = AnimState.Small) {
        radiusState using tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
        colorState using tween(
            easing = LinearOutSlowInEasing
        )
        positionState using tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CirclePreview() {
    ComposeChallengesTheme {
        Column {

            val circleState = remember { mutableStateOf(AnimState.Small) }
            CircleAnim(circleState.value)

            Divider()

            Button(onClick = {
                circleState.value = circleState.value.next()
            }, modifier = Modifier.padding(16.dp)) {
                Text("Animate!")
            }
        }
    }
}