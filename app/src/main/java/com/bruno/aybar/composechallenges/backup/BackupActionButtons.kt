package com.bruno.aybar.composechallenges.backup

import androidx.compose.animation.core.*
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.bruno.aybar.composechallenges.common.AnimationStateHolder
import com.bruno.aybar.composechallenges.ui.buttonHeight

private data class ButtonsUiProperties(
    val backupButtonAlpha: Float,
    val backupButtonSizeMultiplier: Float,
    val cancelButtonAlpha: Float,
    val cancelButtonWidth: Int,
)

enum class ButtonsState {
    CreateBackup,
    Cancel
}

class BackupActionButtonState: AnimationStateHolder<ButtonsState>(
    initialState = ButtonsState.CreateBackup
){

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

    val transition: Transition<ButtonsState> = updateTransition(stateHolder.state)
    val animationDuration = DefaultDurationMillis

    val backupButtonAlpha: Float by transition.animateFloat(
            transitionSpec = {
                if(ButtonsState.CreateBackup isTransitioningTo ButtonsState.Cancel) {
                    tween(delayMillis = animationDuration, easing = LinearEasing)
                } else {
                    tween(easing = LinearEasing)
                }
            },
            targetValueByState = { if(it == ButtonsState.CreateBackup) 1f else 0f }
    )

    val backupButtonSizeMultiplier: Float by transition.animateFloat(
            transitionSpec = {
                if(ButtonsState.CreateBackup isTransitioningTo ButtonsState.Cancel) {
                    keyframes { 0.95f at animationDuration / 2 }
                } else {
                    keyframes { 1.05f at animationDuration / 2 }
                }
            },
            targetValueByState = { 1f }
    )

    val cancelButtonAlpha: Float by transition.animateFloat(
            transitionSpec = {
                if(ButtonsState.CreateBackup isTransitioningTo ButtonsState.Cancel) {
                    tween(delayMillis = animationDuration, easing = LinearEasing)
                } else {
                    tween(easing = LinearEasing)
                }
            },
            targetValueByState = { if(it == ButtonsState.CreateBackup) 0f else 0.6f }
    )

    val cancelButtonWidth: Int by transition.animateInt(
            transitionSpec = {
                if(ButtonsState.CreateBackup isTransitioningTo ButtonsState.Cancel) {
                    tween(delayMillis = animationDuration + 150)
                } else {
                    tween()
                }
            },
            targetValueByState = { if(it == ButtonsState.CreateBackup) 180 else 150 }
    )

    val properties = ButtonsUiProperties(
            backupButtonAlpha = backupButtonAlpha,
            backupButtonSizeMultiplier = backupButtonSizeMultiplier,
            cancelButtonAlpha = cancelButtonAlpha,
            cancelButtonWidth = cancelButtonWidth
    )

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
            .preferredWidth(240.dp * properties.backupButtonSizeMultiplier)
            .preferredHeight(buttonHeight * properties.backupButtonSizeMultiplier),
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
            .preferredWidth(properties.cancelButtonWidth.dp)
            .preferredHeight(buttonHeight),
    ) {
        Text(text = "Cancel")
    }
}