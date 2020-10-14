@file:Suppress("LocalVariableName")

package com.bruno.aybar.composechallenges.backup

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawOpacity
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.dp
import com.bruno.aybar.composechallenges.common.AnimationStateHolder
import com.bruno.aybar.composechallenges.common.transition
import com.bruno.aybar.composechallenges.ui.buttonHeight
import com.bruno.aybar.composechallenges.ui.typography
import kotlin.math.tan

private val completedProgress = FloatPropKey()
private val topSpacing = IntPropKey()
private val textSeparation = IntPropKey()
private val buttonBottom = IntPropKey()

enum class BackupCompletedState {
    HIDDEN, VISIBLE
}

class AnimatedCompletedState: AnimationStateHolder<BackupCompletedState>(
    initialState = BackupCompletedState.HIDDEN
) {

    fun update(ui: BackupUi) {
        animateTo(newState = when(ui) {
            is BackupUi.BackupCompleted -> BackupCompletedState.VISIBLE
            else -> BackupCompletedState.HIDDEN
        })
    }

}

@Composable
fun BackupCompleted(ui: BackupUi, modifier: Modifier, onOk: ()->Unit) {
    val state = remember { AnimatedCompletedState() }

    val transition = transition(
        definition = completedAnimation,
        stateHolder = state
    )

    state.update(ui)

    ConstraintLayout(modifier) {
        val (checkRef, textRef, buttonRef) = createRefs()

        AnimatedCheck(transition[completedProgress], Modifier.constrainAs(checkRef) {
            bottom.linkTo(textRef.top)
            centerHorizontallyTo(parent)
        })

        CompletedHint(modifier = Modifier.constrainAs(textRef) {
            centerVerticallyTo(parent)
            centerHorizontallyTo(parent)
        }, transition = transition)

        OkButton(onClick = onOk, modifier = Modifier.constrainAs(buttonRef) {
            bottom.linkTo(parent.bottom, margin = transition[buttonBottom].dp)
            centerHorizontallyTo(parent)
        }, transition = transition)

    }

}

@Composable
private fun OkButton(modifier: Modifier, onClick: () -> Unit, transition: TransitionState) {
    OutlinedButton(
        onClick = onClick,
        backgroundColor = Color.White,
        modifier = modifier
            .preferredWidth(150.dp)
            .preferredHeight(buttonHeight)
            .drawOpacity(transition[completedProgress])
    ) {
        Text("Ok")
    }
}

@Composable
private fun CompletedHint(modifier: Modifier, transition: TransitionState) {
    Column(modifier.height(100.dp) , horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(transition[topSpacing].dp))
        Text("data has successfully",
            style = typography.subtitle2,
            modifier = Modifier.drawOpacity(transition[completedProgress])
        )
        Spacer(Modifier.height(transition[textSeparation].dp))
        Text("uploaded",
            style = typography.subtitle2,
            modifier = Modifier.drawOpacity(transition[completedProgress])
        )
    }
}


private val tan1 = tan(Math.toRadians(45.0)).toFloat()
private val tan2 = tan(Math.toRadians(45.0)).toFloat()

@Composable
private fun AnimatedCheck(progress: Float, modifier: Modifier) {
    val color = MaterialTheme.colors.primary
    val stroke = with(DensityAmbient.current) { Stroke(2.dp.toPx()) }

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

private val completedAnimation = transitionDefinition<BackupCompletedState> {
    state(BackupCompletedState.HIDDEN) {
        this[completedProgress] = 0f
        this[topSpacing] = 36
        this[textSeparation] = 16
        this[buttonBottom] = 8
    }
    state(BackupCompletedState.VISIBLE) {
        this[completedProgress] = 1f
        this[topSpacing] = 20
        this[textSeparation] = 4
        this[buttonBottom] = 24
    }

    val duration = 1000
    val delay = 1000 // wait for cloud progress -> covering animation
    transition(fromState = BackupCompletedState.HIDDEN, toState = BackupCompletedState.VISIBLE) {
        completedProgress using tween(duration, delay, easing = LinearOutSlowInEasing)
        topSpacing using tween(duration, delay, easing = LinearOutSlowInEasing)
        textSeparation using tween(duration, delay, easing = LinearOutSlowInEasing)
        buttonBottom using tween(duration, delay, easing = LinearOutSlowInEasing)
    }

    transition(fromState = BackupCompletedState.VISIBLE, toState = BackupCompletedState.HIDDEN) {
        completedProgress using tween(duration, easing = LinearOutSlowInEasing)
        topSpacing using tween(duration, easing = LinearOutSlowInEasing)
        textSeparation using tween(duration, easing = LinearOutSlowInEasing)
        buttonBottom using tween(duration, easing = LinearOutSlowInEasing)
    }
}
