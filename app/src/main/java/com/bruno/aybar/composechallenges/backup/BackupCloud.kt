package com.bruno.aybar.composechallenges.backup

import androidx.compose.animation.core.*
import androidx.compose.animation.transition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Radius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.bruno.aybar.composechallenges.ui.ComposeChallengesTheme

enum class CloudState {
    CLOUD, MERGED,
}

private val cloudColor = Color.White
private const val expandedSize = 150f

private val circleSize = FloatPropKey()
private val sideCloudsOffset = FloatPropKey()

class AnimatedCloudState {
    var current by mutableStateOf(CloudState.CLOUD)
        private set
    var animatingTo by mutableStateOf(CloudState.CLOUD)
        private set

    fun update(ui :BackupUi) {
        val newState = when(ui) {
            is BackupUi.RequestBackup -> CloudState.CLOUD
            else -> CloudState.MERGED
        }
        if(newState != current) {
            animatingTo = newState
        }
    }

    fun onAnimationCompleted() {
        current = animatingTo
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
        val actualRadius = baseRadius * transition[circleSize]

        drawCircle(
            color = cloudColor,
            radius = actualRadius
        )

        drawSideClouds(
            baseRadius,
            actualRadius,
            transition[sideCloudsOffset]
        )
    }
}

private val cloudAnimation = transitionDefinition<CloudState> {
    state(CloudState.CLOUD) {
        this[circleSize] = 1f
        this[sideCloudsOffset] = 0f
    }
    state(CloudState.MERGED) {
        this[circleSize] = 1.5f
        this[sideCloudsOffset] = 1f
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
}

private fun DrawScope.drawSideClouds(
    baseRadius: Float,
    actualRadius: Float,
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


@Preview
@Composable
private fun CloudPreview() {
    ComposeChallengesTheme {
        BackupCloud(BackupUi.RequestBackup(""), Modifier
            .width(500.dp)
            .height(300.dp))
    }
}