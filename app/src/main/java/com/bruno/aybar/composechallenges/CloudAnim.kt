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

@Composable
fun CloudAnim(cloudState: CloudState, modifier: Modifier) {

    val state = transition(
        definition = remember { cloudTransition() },
        toState = cloudState
    )

    Canvas(modifier) {

        var mainCircleSize = bigCircleRadius *
            (1 + state[cloudMergeProgress] + state[bubblesProgress])
//            .pow(1 + state[coveringProgress])
        val mainCircleCenter = Offset(
            x = center.x,
            y = center.y - (mainCircleSize * state[bubblesProgress])
        )

//        clipRect(
//            left = mainCircleCenter.x - mainCircleSize,
//            right = mainCircleCenter.x + mainCircleSize,
//            bottom = mainCircleCenter.y + mainCircleSize,
//            top = mainCircleCenter.y - mainCircleSize
//        ) {
//        }
        drawCircle(color = cloudColor, radius = mainCircleSize, center = mainCircleCenter)

        when(cloudState) {
            CloudState.Normal, CloudState.Merging -> drawSideClouds(state[cloudMergeProgress])
            CloudState.Bubbling -> drawBubbles(
                mainCircleCenter = mainCircleCenter,
                mainCircleSize = mainCircleSize,
                bubblesProgress = state[bubblesProgress]
            )
            CloudState.Expanding -> { }
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
    bubblesProgress: Float
) {
    val circleBottom = mainCircleCenter.y + mainCircleSize
    clipPath(Path().apply {
        addRoundRect(RoundRect(
            left = mainCircleCenter.x - mainCircleSize,
            right = mainCircleCenter.x + mainCircleSize,
            top = mainCircleCenter.y - mainCircleSize,
            bottom = circleBottom,
            radius = Radius(mainCircleSize, mainCircleSize)
        ))
    }) {
        val relativeProgress = bubblesProgress * 50
        val constantVerticalSpeed = 30
        bubbles.forEach {
            drawCircle(
                color = it.color,
                radius = it.size,
                center = Offset(
                    x = mainCircleCenter.x + it.direction * it.speed * bubblesProgress * relativeProgress,
                    y = circleBottom + it.initialPosition - relativeProgress * constantVerticalSpeed
                )
            )
        }

    }
}

val bubbles = (0..500).map {
    Bubble(
        size = 30f + Random.nextInt(from = 10, until = 30),
        color = when(Random.nextInt(5)) {
            1,2 -> purple1
            3,4 -> purple2
            else -> purple3
        },
        direction = if(Random.nextBoolean()) 1f else -1f,
        speed = Random.nextInt(3, 10).toFloat(),
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
    Normal, Merging, Bubbling, Expanding;
}

private fun cloudTransition() = transitionDefinition<CloudState> {
    state(CloudState.Normal) {
        this[cloudMergeProgress] = 0f
        this[bubblesProgress] = 0f
        this[coveringProgress] = 0f
    }
    state(CloudState.Merging) {
        this[cloudMergeProgress] = 1f
        this[bubblesProgress] = 0f
        this[coveringProgress] = 0f
    }
    state(CloudState.Bubbling) {
        this[cloudMergeProgress] = 1f
        this[bubblesProgress] = 1f
        this[coveringProgress] = 0f
    }
    state(CloudState.Expanding) {
        this[cloudMergeProgress] = 1f
        this[bubblesProgress] = 1f
        this[coveringProgress] = 1f
    }

    transition(fromState = CloudState.Normal, toState = CloudState.Merging) {
        cloudMergeProgress using tween(durationMillis = 1000)
    }
    transition(fromState = CloudState.Merging, toState = CloudState.Normal) {
        cloudMergeProgress using tween(durationMillis = 1000)
    }
    transition(fromState = CloudState.Merging, toState = CloudState.Bubbling) {
        bubblesProgress using tween(durationMillis = 3000, easing = FastOutLinearInEasing)
    }
    transition(fromState = CloudState.Bubbling, toState = CloudState.Merging) {
        bubblesProgress using tween(durationMillis = 3000, easing = LinearOutSlowInEasing)
    }
}


@Preview(showBackground = true)
@Composable
private fun CirclePreview() {
    ComposeChallengesTheme {
        Surface {
            val cloudState = remember { mutableStateOf(CloudState.Merging) }

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
            onClick = { state.value = CloudState.Merging },
            modifier = Modifier
                .preferredWidth(150.dp)
                .preferredHeight(60.dp)
                .gravity(Alignment.Center)
                .drawOpacity(buttonsState[cancelButtonAlpha])
        )
        if(buttonsState[backupButtonAlpha] > 0) {
            Button(
                onClick = { state.value = CloudState.Bubbling },
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
    state(CloudState.Merging) {
        this[backupButtonAlpha] = 1f
        this[cancelButtonAlpha] = 0.5f
    }
    state(CloudState.Bubbling) {
        this[backupButtonAlpha] = 0f
        this[cancelButtonAlpha] = 1f
    }
}