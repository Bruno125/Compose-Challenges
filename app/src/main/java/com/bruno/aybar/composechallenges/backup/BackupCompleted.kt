@file:Suppress("LocalVariableName")

package com.bruno.aybar.composechallenges.backup

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.dp
import com.bruno.aybar.composechallenges.common.AnimationStateHolder
import com.bruno.aybar.composechallenges.ui.buttonHeight
import com.bruno.aybar.composechallenges.ui.typography
import kotlin.math.tan

private data class BackupCompletedUiProperties(
    val completedProgress: Float,
    val topSpacing: Int,
    val textSeparation: Int,
    val buttonBottom: Int,
)

enum class BackupCompletedState {
    HIDDEN, VISIBLE
}

class AnimatedCompletedState: AnimationStateHolder<BackupCompletedState>(
    initialState = BackupCompletedState.HIDDEN
) {

    fun update(ui: BackupUi) {
        current = when(ui) {
            is BackupUi.BackupCompleted -> BackupCompletedState.VISIBLE
            else -> BackupCompletedState.HIDDEN
        }
    }

}

@Composable
fun BackupCompleted(ui: BackupUi, modifier: Modifier, onOk: ()->Unit) {
    val state = remember { AnimatedCompletedState() }
    state.update(ui)

    val transition: Transition<BackupCompletedState> = updateTransition(targetState = state.current)

    val floatSpec = AnimationSpecBuilder<Float>()
    val intSpec = AnimationSpecBuilder<Int>()

    val completedProgress: Float by transition.animateFloat(
        transitionSpec = { with(floatSpec) { buildAnimationSpec() } },
        targetValueByState = { if(it == BackupCompletedState.HIDDEN) 0f else 1f }
    )
    val topSpacing: Int by transition.animateInt(
        transitionSpec = { with(intSpec) { buildAnimationSpec() } },
        targetValueByState = { if(it == BackupCompletedState.HIDDEN) 36 else 20 }
    )
    val textSeparation: Int by transition.animateInt(
        transitionSpec = { with(intSpec) { buildAnimationSpec() } },
        targetValueByState = { if(it == BackupCompletedState.HIDDEN) 16 else 4 }
    )
    val buttonBottom: Int by transition.animateInt(
        transitionSpec = { with(intSpec) { buildAnimationSpec() } },
        targetValueByState = { if(it == BackupCompletedState.HIDDEN) 8 else 24 }
    )

    val properties = BackupCompletedUiProperties(
        completedProgress = completedProgress,
        topSpacing = topSpacing,
        textSeparation = textSeparation,
        buttonBottom = buttonBottom,
    )

    ConstraintLayout(modifier) {
        val (checkRef, textRef, buttonRef) = createRefs()

        AnimatedCheck(properties.completedProgress, Modifier.constrainAs(checkRef) {
            bottom.linkTo(textRef.top)
            centerHorizontallyTo(parent)
        })

        CompletedHint(modifier = Modifier.constrainAs(textRef) {
            centerVerticallyTo(parent)
            centerHorizontallyTo(parent)
        }, properties = properties)

        OkButton(onClick = onOk, modifier = Modifier.constrainAs(buttonRef) {
            bottom.linkTo(parent.bottom, margin = properties.buttonBottom.dp)
            centerHorizontallyTo(parent)
        }, properties = properties)

    }

}

@Composable
private fun OkButton(modifier: Modifier, onClick: () -> Unit, properties: BackupCompletedUiProperties) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .preferredWidth(150.dp)
            .preferredHeight(buttonHeight)
            .alpha(properties.completedProgress)
    ) {
        Text("Ok")
    }
}

@Composable
private fun CompletedHint(modifier: Modifier, properties: BackupCompletedUiProperties) {
    Column(modifier.height(100.dp) , horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(properties.topSpacing.dp))
        Text("data has successfully",
            style = typography.subtitle2,
            modifier = Modifier.alpha(properties.completedProgress)
        )
        Spacer(Modifier.height(properties.textSeparation.dp))
        Text("uploaded",
            style = typography.subtitle2,
            modifier = Modifier.alpha(properties.completedProgress)
        )
    }
}


private val tan1 = tan(Math.toRadians(45.0)).toFloat()
private val tan2 = tan(Math.toRadians(45.0)).toFloat()

@Composable
private fun AnimatedCheck(progress: Float, modifier: Modifier) {
    val color = MaterialTheme.colors.primary
    val stroke = with(AmbientDensity.current) { Stroke(2.dp.toPx()) }

    Canvas(modifier.size(100.dp)) {
        drawArc(color,
            startAngle = 270f,
            sweepAngle = progress * 360,
            useCenter = false,
            style = stroke
        )

        drawAnimatedCheck(progress, stroke, color)

    }
}

private fun DrawScope.drawAnimatedCheck(progress: Float, stroke: Stroke, color: Color) {
    val w1 = 20f
    val w2 = 55f
    val h1 = w1 * tan1
    val h2 = w2 * tan2
    val totalW = w1 + w2
    val totalH = h2
    val centinel = totalW * progress

    val line1_start = Offset(x = center.x - totalW / 2, y = center.y + (totalH / 2f) - h1)
    val line1_end = Offset(
        x = line1_start.x + centinel.coerceAtMost(w1),
        y = line1_start.y + (centinel * tan1).coerceAtMost(h1)
    )

    drawLine(color, line1_start, line1_end, strokeWidth = stroke.width)

    val line2_progress = centinel - w1
    if(line2_progress < 0) return

    val line2_start = line1_end
    val line2_end = Offset(
        x = line2_start.x + line2_progress,
        y = line2_start.y - (line2_progress * tan2).coerceAtMost(h2)
    )
    drawLine(color, line2_start, line2_end, strokeWidth = stroke.width)
}

private class AnimationSpecBuilder<T> {
    private val duration = 1000
    private val delay = 1000 // wait for cloud progress -> covering animation

    fun Transition.Segment<BackupCompletedState>.buildAnimationSpec(): FiniteAnimationSpec<T> {
        return if(BackupCompletedState.HIDDEN isTransitioningTo BackupCompletedState.VISIBLE) {
            tween(duration, delay, easing = LinearOutSlowInEasing)
        } else {
            tween(duration, easing = LinearOutSlowInEasing)
        }
    }
}
