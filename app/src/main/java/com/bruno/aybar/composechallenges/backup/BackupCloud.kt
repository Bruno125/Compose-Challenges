package com.bruno.aybar.composechallenges.backup

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

private class CloudUiProperties(
    circleSize: State<Float>,
    sideCloudsOffset: State<Float>,
    exitBubblesProgress: State<Float>,
    progress: State<Float>,
) {
    val circleSize: Float by circleSize
    val sideCloudsOffset: Float by sideCloudsOffset
    val exitBubblesProgress: Float by exitBubblesProgress
    val progress: Float by progress
}

class AnimatedCloudState {
    var state: CloudState = CloudState.CLOUD; private set

    private val _progress = mutableStateOf(0f)
    val progress: State<Float> = _progress

    fun update(ui :BackupUi) {
        updateProgress(ui)
        state = when(ui) {
            is BackupUi.RequestBackup -> CloudState.CLOUD
            is BackupUi.BackupInProgress -> CloudState.MERGED
            is BackupUi.BackupCompleted -> CloudState.COVERING
        }
    }

    private fun updateProgress(ui: BackupUi) {
        if(ui is BackupUi.BackupInProgress) {
            _progress.value = ui.progress / 100f
        }
    }

}

@Composable
fun BackupCloud(ui: BackupUi, modifier: Modifier) {

    val cloudState = remember { AnimatedCloudState() }
    cloudState.update(ui)

    val properties = buildUiProperties(cloudState)
    BackupCloudContent(properties, modifier)
}

@Composable
private fun BackupCloudContent(properties: CloudUiProperties, modifier: Modifier) {

    Canvas(modifier) {
        val baseRadius = size.height / 4
        val multiplier = properties.circleSize + properties.progress * 1.5f
        val actualRadius = baseRadius * multiplier

        val verticalCenter = if(properties.progress != 0f) {
            val baseline = center.y + baseRadius * properties.circleSize
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

        drawSideClouds(baseRadius, properties.sideCloudsOffset)

        if(properties.progress != 0f) {
            drawBubbles(
                baseline = center.y + baseRadius * properties.circleSize.coerceAtMost(1.5f),
                mainCircleCenter = Offset(center.x, verticalCenter),
                mainCircleSize = actualRadius,
                progress = properties.progress + properties.exitBubblesProgress
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
        cornerRadius = CornerRadius(rightRadius),
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
        cornerRadius = CornerRadius(leftRadius),
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
                cornerRadius = CornerRadius(mainCircleSize, mainCircleSize)
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

@Composable
private fun buildUiProperties(stateHolder: AnimatedCloudState): CloudUiProperties {
    val transition: Transition<CloudState> = updateTransition(stateHolder.state, label = "cloudTransition")
    val defaultDuration = 1000
    val circleSize = transition.animateFloat(
        transitionSpec = {
            if(CloudState.MERGED isTransitioningTo CloudState.COVERING) {
                tween(delayMillis = 300, durationMillis = defaultDuration)
            } else {
                tween(defaultDuration)
            }
        },
        targetValueByState = {
            when(it) {
                CloudState.CLOUD -> 1f
                CloudState.MERGED -> 1.5f
                CloudState.COVERING -> 10F
            }
        }, label = "circleSize"
    )

    val sideCloudsOffset = transition.animateFloat(
        transitionSpec = {
            if(CloudState.MERGED isTransitioningTo CloudState.COVERING) {
                tween(delayMillis = 300)
            } else {
                tween(defaultDuration)
            }
        },
        targetValueByState = {
            when(it) {
                CloudState.CLOUD -> 0f
                CloudState.MERGED -> 1f
                else -> 1f
            }
        }, label = "sideCloudsOffset"
    )

    val exitBubblesProgress = transition.animateFloat(
        transitionSpec = {
            if(CloudState.MERGED isTransitioningTo CloudState.COVERING) {
                tween(durationMillis = 1500)
            } else {
                tween(defaultDuration)
            }
        },
        targetValueByState = {
            when(it) {
                CloudState.COVERING -> 0.8f
                else -> 0f
            }
        }, label = "exitBubblesProgress"
    )

    return CloudUiProperties(
        circleSize = circleSize,
        sideCloudsOffset = sideCloudsOffset,
        exitBubblesProgress = exitBubblesProgress,
        progress = stateHolder.progress
    )

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