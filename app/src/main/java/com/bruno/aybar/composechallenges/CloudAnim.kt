package com.bruno.aybar.composechallenges

import androidx.compose.animation.core.*
import androidx.compose.animation.transition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ColumnScope.gravity
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Radius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.MutableState
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawOpacity
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.ui.tooling.preview.Devices.AUTOMOTIVE_1024p
import com.bruno.aybar.composechallenges.ui.*
import kotlin.math.pow
import kotlin.random.Random

private val cloudColor = Color.White

private val bigCircleRadius = 200f
private val smallCircleSize = 220f
private val mediumCircleSize = 270f
private val expandedSize = 120f

private val cloudMergeProgress = FloatPropKey()
private val bubblesProgress = FloatPropKey()
private val coveringProgress = FloatPropKey()

private fun Float.plus(value: Float, givenThat: ()->Boolean): Float {
    return this + if(givenThat()) value else 0f
}

@Composable
fun CloudAnim(cloudState: CloudState, modifier: Modifier) {

    val state = transition(
        definition = remember { cloudTransition() },
        toState = cloudState
    )

    fun isBubbling() = state[bubblesProgress] > 0
    fun isCovering() = state[coveringProgress] > 0

    Canvas(modifier) {

        var sizeMultiplier = (1f + state[cloudMergeProgress])
            .plus(state[bubblesProgress], givenThat = { isBubbling() })
        val verticalOffset = bigCircleRadius * sizeMultiplier * state[bubblesProgress];
        sizeMultiplier = sizeMultiplier.plus(state[coveringProgress] * 100, givenThat = { isCovering() })

        val mainCircleSize = bigCircleRadius * sizeMultiplier
        val mainCircleCenter = Offset(
            x = center.x,
            y = center.y - verticalOffset
        )
        drawCircle(color = cloudColor, radius = mainCircleSize, center = mainCircleCenter)

        when(cloudState) {
            CloudState.Normal -> drawSideClouds(state[cloudMergeProgress])
            CloudState.Animating -> {
                if(state[bubblesProgress] == 0f) {
                    drawSideClouds(state[cloudMergeProgress])
                }
                drawBubbles(
                    mainCircleCenter = mainCircleCenter,
                    mainCircleSize = mainCircleSize,
                    progress = state[bubblesProgress] + state[coveringProgress]
                )
            }
            CloudState.End -> { }
        }
    }
}

private fun DrawScope.drawSideClouds(mergeProgress: Float) {
    val leftCloudSize = Size(smallCircleSize + expandedSize, smallCircleSize)
    val leftCloudAnimationOffset = mergeProgress * 30
    drawRoundRect(
        color = cloudColor,
        size = leftCloudSize,
        radius = Radius(smallCircleSize / 2),
        topLeft = Offset(
            x = center.x - smallCircleSize - expandedSize / 2 + leftCloudAnimationOffset,
            y = center.y + bigCircleRadius - smallCircleSize
        )
    )

    val rightCloudSize = Size(mediumCircleSize + expandedSize, mediumCircleSize)
    val rightCloudAnimOffset = mergeProgress * 30
    drawRoundRect(
        color = cloudColor,
        size = rightCloudSize,
        radius = Radius(mediumCircleSize / 2),
        topLeft = Offset(
            x = center.x - expandedSize - rightCloudAnimOffset,
            y = center.y + bigCircleRadius - mediumCircleSize
        )
    )
}

private fun DrawScope.drawBubbles(
    mainCircleCenter: Offset,
    mainCircleSize: Float,
    progress: Float
) {
    val circleBottom = mainCircleCenter.y + mainCircleSize
    val relativeProgress = progress * 100
    println("Relative progress: $relativeProgress")

    clipPath(Path().apply {
        addRoundRect(RoundRect(
            left = mainCircleCenter.x - mainCircleSize,
            right = mainCircleCenter.x + mainCircleSize,
            top = mainCircleCenter.y - mainCircleSize,
            bottom = circleBottom,
            radius = Radius(mainCircleSize, mainCircleSize)
        ))
    }) {
        val constantVerticalSpeed = 30

        bubbles.forEach {
            val verticalPosition = circleBottom +
                it.size +
                it.initialPosition -
                relativeProgress * constantVerticalSpeed

            val isOutSide = verticalPosition > circleBottom
            val horizontalOffset = if(isOutSide) 0f else {
                it.direction * it.speed * (progress) * relativeProgress
            }

            drawCircle(
                color = it.color,
                radius = it.size,
                center = Offset(
                    x = mainCircleCenter.x + horizontalOffset,
                    y = verticalPosition
                )
            )
        }

    }
}

val bubbles = (0..100).map {
    Bubble(
        size = 30f + Random.nextInt(from = 10, until = 30),
        color = when(Random.nextInt(5)) {
            1,2 -> purple1
            3,4 -> purple2
            else -> purple3
        },
        direction = if(Random.nextBoolean()) 1f else -1f,
        speed = Random.nextInt(1, 10).toFloat(),
        initialPosition = it * 20f,
    )
}

data class Bubble(
    val color: Color,
    val direction: Float,
    val speed: Float,
    val size: Float,
    val initialPosition: Float
)

enum class CloudState {
    Normal, Animating, End;
}

private fun cloudTransition() = transitionDefinition<CloudState> {
    state(CloudState.Normal) {
        this[cloudMergeProgress] = 0f
        this[bubblesProgress] = 0f
        this[coveringProgress] = 0f
    }
    state(CloudState.Animating) {
        this[cloudMergeProgress] = 1f
        this[bubblesProgress] = 1.5f
        this[coveringProgress] = 1f
    }
    state(CloudState.End) {
        this[cloudMergeProgress] = 1f
        this[bubblesProgress] = 1f
        this[coveringProgress] = 0f
    }

    transition(fromState = CloudState.Normal, toState = CloudState.Animating) {
        cloudMergeProgress using tween(delayMillis = 0, durationMillis = 1000)
        bubblesProgress using tween(delayMillis = 1000, durationMillis = 3500, easing = LinearEasing)
        coveringProgress using tween(delayMillis = 4000, durationMillis = 10000, easing = LinearEasing)
    }
    transition(fromState = CloudState.Animating, toState = CloudState.Normal) {
        cloudMergeProgress using tween(durationMillis = 0, )
        bubblesProgress using tween(durationMillis = 0, )
        coveringProgress using tween(durationMillis = 0, )
    }
}


@Preview(showBackground = true)
@Composable
private fun CirclePreview() {
    ComposeChallengesTheme {
        Surface {
            val cloudState = remember { mutableStateOf(CloudState.Normal) }

            ConstraintLayout(Modifier.fillMaxSize()) {
                val (titleRef, cloudRef, backupRef) = createRefs()

                Text("Cloud Storage", style = typography.subtitle1,
                    modifier = Modifier.constrainAs(titleRef) {
                        top.linkTo(parent.top)
                    }
                )

                CloudAnim(cloudState.value, modifier = Modifier
                    .constrainAs(cloudRef) {
                        top.linkTo(titleRef.bottom)
                        bottom.linkTo(backupRef.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    })

                AnimateButtons(cloudState, modifier = Modifier.padding(16.dp)
                    .constrainAs(backupRef) {
                        bottom.linkTo(parent.bottom)
                        centerHorizontallyTo(parent)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )

            }
        }
    }
}

private val backupButtonAlpha = FloatPropKey()
private val cancelButtonAlpha = FloatPropKey()

@Composable
fun AnimateButtons(state: MutableState<CloudState>, modifier: Modifier) {
    val buttonsState = transition(
        definition = AnimateButtonsTransition,
        toState = state.value
    )

    Stack(modifier) {
        CancelButton(
            onClick = { state.value = CloudState.Normal },
            modifier = Modifier
                .preferredWidth(150.dp)
                .preferredHeight(60.dp)
                .gravity(Alignment.Center)
                .drawOpacity(buttonsState[cancelButtonAlpha])
        )
        if(buttonsState[backupButtonAlpha] > 0) {
            Button(
                onClick = { state.value = CloudState.Animating },
                modifier = Modifier
                    .preferredWidth(240.dp)
                    .preferredHeight(60.dp)
                    .gravity(Alignment.Center)
                    .drawOpacity(buttonsState[backupButtonAlpha])
            ) {
                Text("Create Backup")
            }
        }
    }
}

private val AnimateButtonsTransition = transitionDefinition<CloudState> {
    state(CloudState.Normal) {
        this[backupButtonAlpha] = 1f
        this[cancelButtonAlpha] = 0.5f
    }
    state(CloudState.Animating) {
        this[backupButtonAlpha] = 0f
        this[cancelButtonAlpha] = 1f
    }
}