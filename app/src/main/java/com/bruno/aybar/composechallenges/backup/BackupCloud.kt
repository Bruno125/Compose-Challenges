package com.bruno.aybar.composechallenges.backup

import androidx.compose.animation.core.*
import androidx.compose.animation.transition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Radius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.bruno.aybar.composechallenges.ui.ComposeChallengesTheme
import com.bruno.aybar.composechallenges.ui.purple1
import com.bruno.aybar.composechallenges.ui.purple2
import com.bruno.aybar.composechallenges.ui.purple3
import kotlin.random.Random

enum class CloudState {
    CLOUD, MERGED, COVERING
}

private val cloudColor = Color.White
private const val expandedSize = 120f

private val circleSize = FloatPropKey()
private val sideCloudsOffset = FloatPropKey()
private val exitBubblesProgress = FloatPropKey()

class AnimatedCloudState {
    var current by mutableStateOf(CloudState.CLOUD)
        private set
    var animatingTo by mutableStateOf(CloudState.CLOUD)
        private set
    var progress = 0f
        private set

    fun update(ui :BackupUi) {
        updateProgress(ui)
        val newState = when(ui) {
            is BackupUi.RequestBackup -> CloudState.CLOUD
            is BackupUi.BackupInProgress -> CloudState.MERGED
            is BackupUi.BackupCompleted -> CloudState.COVERING
        }
        if(newState != current) {
            animatingTo = newState
        }
    }

    fun onAnimationCompleted() {
        current = animatingTo
    }

    private fun updateProgress(ui: BackupUi) {
        if(ui is BackupUi.BackupInProgress) {
            progress = ui.progress / 100f
        }
    }

}

@Composable
fun BackupCloud(ui: BackupUi, modifier: Modifier) {

    val cloudState = remember { AnimatedCloudState() }
    cloudState.update(ui)

    val transition = transition(
        definition = cloudAnimation,
        initState = cloudState.current,
        toState = cloudState.animatingTo,
        onStateChangeFinished = { cloudState.onAnimationCompleted() }
    )

    Canvas(modifier) {
        val baseRadius = size.height / 4
        val multiplier = transition[circleSize] + cloudState.progress * 1.5f
        val actualRadius = baseRadius * multiplier

        val verticalCenter = if(cloudState.progress != 0f) {
            val baseline = center.y + baseRadius * transition[circleSize]
            baseline - actualRadius
        } else center.y

        drawCircle(
            color = cloudColor,
            radius = actualRadius,
            center = Offset(
                center.x,
                verticalCenter
            )
        )

        drawSideClouds(baseRadius, transition[sideCloudsOffset])

        if(cloudState.progress != 0f) {
            drawBubbles(
                baseline = center.y + baseRadius * transition[circleSize].coerceAtMost(1.5f),
                mainCircleCenter = Offset(center.x, verticalCenter),
                mainCircleSize = actualRadius,
                progress = cloudState.progress + transition[exitBubblesProgress]
            )
        }
    }
}

private fun DrawScope.drawSideClouds(
    baseRadius: Float,
    mergeProgress: Float
) {
    val bottom = center.y + baseRadius

    val rightRadius = baseRadius * 0.75f
    val rightWidth = expandedSize + rightRadius * 2f
    val rightHeight = rightRadius * 2
    val rightHorizontalOffset = rightRadius * mergeProgress
    drawRoundRect(
        color = cloudColor,
        size = Size(rightWidth, rightHeight),
        radius = Radius(rightRadius),
        topLeft = Offset(
            x = center.x - expandedSize - rightHorizontalOffset,
            y = bottom - rightHeight
        )
    )

    val leftRadius = baseRadius * 0.65f
    val leftWidth = expandedSize + leftRadius * 2f
    val leftHeight = leftRadius * 2f
    val leftCloudAnimationOffset = leftRadius * mergeProgress
    drawRoundRect(
        color = cloudColor,
        size = Size(leftWidth, leftHeight),
        radius = Radius(leftRadius),
        topLeft = Offset(
            x = center.x - leftWidth + leftRadius * 0.5f + leftCloudAnimationOffset,// - leftCircleSize - expandedSize / 2 - leftCloudAnimationOffset,
            y = bottom - leftHeight
        )
    )

}

private fun DrawScope.drawBubbles(
    baseline: Float,
    mainCircleCenter: Offset,
    mainCircleSize: Float,
    progress: Float
) {
    val relativeProgress = progress * 100 / 1.5f

    clipPath(Path().apply {
        addRoundRect(
            RoundRect(
                left = mainCircleCenter.x - mainCircleSize,
                right = mainCircleCenter.x + mainCircleSize,
                top = mainCircleCenter.y - mainCircleSize,
                bottom = mainCircleCenter.y + mainCircleSize,
                radius = Radius(mainCircleSize, mainCircleSize)
            )
        )
    }) {

        bubbles.forEach {

            val verticalPosition = baseline +
                it.size +
                it.initialPosition -
                relativeProgress * it.verticalSpeed

            val isOutSide = verticalPosition > baseline
            val horizontalOffset = if(isOutSide) 0f else {
                it.direction * it.horizontalSpeed * (progress) * relativeProgress
            }
            if(!isOutSide) {
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
}

val bubbles = (0..250).map {
    Bubble(
        size = 30f + when(it) {
            in 0..20 -> Random.nextInt(from = 10, until = 30)
            in 20..50 -> Random.nextInt(20,50)
            else -> Random.nextInt(0,60)
        },
        color = when(Random.nextInt(6)) {
            1,2 -> purple1
            3,4 -> purple2
            else -> purple3
        },
        direction = if(Random.nextBoolean()) 1f else -1f,
        horizontalSpeed = when(it) {
            in 0..30 -> Random.nextInt(5, 20)
            in 30..80 -> Random.nextInt(1,20)
            else -> Random.nextInt(1,20)
        }.toFloat(),
        verticalSpeed = when(it) {
            in 0..20 -> Random.nextInt(50, 80)
            in 20..60 -> Random.nextInt(60,80)
            else -> Random.nextInt(70,80)
        },
        initialPosition = it * 20f,
    )
}

data class Bubble(
    val color: Color,
    val direction: Float,
    val horizontalSpeed: Float,
    val verticalSpeed: Int,
    val size: Float,
    val initialPosition: Float
)


private val cloudAnimation = transitionDefinition<CloudState> {
    state(CloudState.CLOUD) {
        this[circleSize] = 1f
        this[sideCloudsOffset] = 0f
        this[exitBubblesProgress] = 0f
    }
    state(CloudState.MERGED) {
        this[circleSize] = 1.5f
        this[sideCloudsOffset] = 1f
        this[exitBubblesProgress] = 0f
    }
    state(CloudState.COVERING) {
        this[circleSize] = 10f
        this[sideCloudsOffset] = 1f
        this[exitBubblesProgress] = 0.8f
    }

    val duration = 1000
    transition(fromState = CloudState.CLOUD, toState = CloudState.MERGED) {
        circleSize using tween(durationMillis = duration)
        sideCloudsOffset using tween(durationMillis = duration)
    }
    transition(fromState = CloudState.MERGED, toState = CloudState.CLOUD) {
        circleSize using tween(durationMillis = duration)
        sideCloudsOffset using tween(durationMillis = duration)
    }
    transition(fromState = CloudState.MERGED, toState = CloudState.COVERING) {
        circleSize using tween(delayMillis = 300,durationMillis = duration)
        sideCloudsOffset using tween(delayMillis = 300)
        exitBubblesProgress using tween(1500)
    }
    transition(fromState = CloudState.COVERING, toState = CloudState.MERGED) {
        circleSize using tween(durationMillis = duration)
        sideCloudsOffset using tween(durationMillis = duration)
        exitBubblesProgress using tween(delayMillis = duration)
    }
}

@Preview
@Composable
private fun CloudPreview() {
    ComposeChallengesTheme {
        BackupCloud(BackupUi.RequestBackup(""), Modifier
            .width(500.dp)
            .height(300.dp))
    }
}