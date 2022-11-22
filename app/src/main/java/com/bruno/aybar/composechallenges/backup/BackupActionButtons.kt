package com.bruno.aybar.composechallenges.backup

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.*
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.bruno.aybar.composechallenges.ui.buttonHeight

private class ButtonsUiProperties(
    backupButtonAlpha: State<Float>,
    backupButtonSizeMultiplier: State<Float>,
    cancelButtonAlpha: State<Float>,
    cancelButtonWidth: State<Int>,
) {
    val backupButtonAlpha: Float by backupButtonAlpha
    val backupButtonSizeMultiplier: Float by backupButtonSizeMultiplier
    val cancelButtonAlpha: Float by cancelButtonAlpha
    val cancelButtonWidth: Int by cancelButtonWidth
}

enum class ButtonsState {
    CreateBackup,
    Cancel
}

class BackupActionButtonState {
    var state = ButtonsState.CreateBackup; private set

    fun update(ui: BackupUi) {
        state = when(ui) {
            is BackupUi.RequestBackup -> ButtonsState.CreateBackup
            else -> ButtonsState.Cancel
        }
    }

}


@Composable
fun BackupActionButtons(
    ui: BackupUi,
    onBackup: ()->Unit,
    onCancel: ()->Unit,
    modifier: Modifier
) {
    val stateHolder = remember { BackupActionButtonState() }

    stateHolder.update(ui)

    val properties = buildUiProperties(stateHolder.state)

    Box(modifier) {

        BackupButton(
            onClick = onBackup,
            properties = properties,
            modifier = Modifier.align(Alignment.Center)
        )

        CancelButton(
            onClick = onCancel,
            properties = properties,
            modifier = Modifier.align(Alignment.Center)
        )

    }
}

@Composable
private fun BackupButton(onClick: ()->Unit, properties: ButtonsUiProperties, modifier: Modifier) {
    if(properties.backupButtonAlpha == 0f) return

    Button(
        onClick = onClick,
        modifier = modifier
            .alpha(properties.backupButtonAlpha)
            .requiredWidth(240.dp * properties.backupButtonSizeMultiplier)
            .requiredHeight(buttonHeight * properties.backupButtonSizeMultiplier),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(text = "Create Backup")
    }
}

@Composable
private fun CancelButton(onClick: ()->Unit, properties: ButtonsUiProperties, modifier: Modifier) {
    if(properties.cancelButtonAlpha == 0f) return

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .alpha(properties.cancelButtonAlpha)
            .requiredWidth(properties.cancelButtonWidth.dp)
            .requiredHeight(buttonHeight),
    ) {
        Text(text = "Cancel")
    }
}

@Composable
private fun buildUiProperties(state: ButtonsState): ButtonsUiProperties {

    val transition: Transition<ButtonsState> = updateTransition(state)
    val animationDuration = DefaultDurationMillis

    val backupButtonAlpha = transition.animateFloat(
        transitionSpec = {
            if(ButtonsState.CreateBackup isTransitioningTo ButtonsState.Cancel) {
                tween(delayMillis = animationDuration, easing = LinearEasing)
            } else {
                tween(easing = LinearEasing)
            }
        },
        targetValueByState = { if(it == ButtonsState.CreateBackup) 1f else 0f }, label = ""
    )

    @SuppressLint("UnusedTransitionTargetStateParameter")
    val backupButtonSizeMultiplier = transition.animateFloat(
        transitionSpec = {
            if(ButtonsState.CreateBackup isTransitioningTo ButtonsState.Cancel) {
                keyframes { 0.95f at animationDuration / 2 }
            } else {
                keyframes { 1.05f at animationDuration / 2 }
            }
        },
        targetValueByState = { 1f }, label = "backupButtonSizeMultiplier"
    )

    val cancelButtonAlpha = transition.animateFloat(
        transitionSpec = {
            if(ButtonsState.CreateBackup isTransitioningTo ButtonsState.Cancel) {
                tween(delayMillis = animationDuration, easing = LinearEasing)
            } else {
                tween(easing = LinearEasing)
            }
        },
        targetValueByState = { if(it == ButtonsState.CreateBackup) 0f else 0.6f }, label = "cancelButtonAlpha"
    )

    val cancelButtonWidth = transition.animateInt(
        transitionSpec = {
            if(ButtonsState.CreateBackup isTransitioningTo ButtonsState.Cancel) {
                tween(delayMillis = animationDuration + 150)
            } else {
                tween()
            }
        },
        targetValueByState = { if(it == ButtonsState.CreateBackup) 180 else 150 }, label = "cancelButtonWidth"
    )

    return ButtonsUiProperties(
        backupButtonAlpha = backupButtonAlpha,
        backupButtonSizeMultiplier = backupButtonSizeMultiplier,
        cancelButtonAlpha = cancelButtonAlpha,
        cancelButtonWidth = cancelButtonWidth
    )
}