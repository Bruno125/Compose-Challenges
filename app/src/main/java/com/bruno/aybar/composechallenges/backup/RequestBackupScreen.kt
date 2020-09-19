package com.bruno.aybar.composechallenges.backup

import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.*
import androidx.compose.animation.transition
import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawOpacity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.ui.tooling.preview.Preview
import com.bruno.aybar.composechallenges.ui.BackupButton
import com.bruno.aybar.composechallenges.ui.CancelButton
import com.bruno.aybar.composechallenges.ui.ComposeChallengesTheme
import com.bruno.aybar.composechallenges.ui.typography
import com.bruno.aybar.composechallenges.utils.animationSequence

@Composable
fun RequestBackupScreen() {
    Surface {

        val state = mutableStateOf<BackupUi>(BackupUi.RequestBackup)

        ConstraintLayout(Modifier.fillMaxSize()) {
            val (titleRef, backupRef) = createRefs()

            Text(
                text = "Cloud Storage",
                style = typography.subtitle1,
                modifier = Modifier.padding(16.dp).constrainAs(titleRef) {
                    top.linkTo(parent.top)
                }
            )

            BottomButton(state.value, modifier = Modifier.padding(16.dp)
                .constrainAs(backupRef) {
                    bottom.linkTo(parent.bottom)
                    centerHorizontallyTo(parent)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            , onBackup = {
                state.value = BackupUi.BackupInProgress(0f)
            }, onCancel = {
                state.value = BackupUi.RequestBackup
            }, onOk = {

            })

        }
    }
}

private val backupButtonAlpha = FloatPropKey()
private val backupButtonZIndex = FloatPropKey()

private val cancelButtonAlpha = FloatPropKey()
private val cancelButtonZIndex = FloatPropKey()
private val cancelButtonWidthMultiplier = FloatPropKey()


@Composable
fun BottomButton(
    state: BackupUi,
    onBackup: ()->Unit,
    onCancel: ()->Unit,
    onOk: ()->Unit,
    modifier: Modifier
) {
    val buttonsState = transition(
        definition = AnimateButtonsTransition,
        toState = state.buttonState()
    )

    Stack(modifier) {

        CancelButton(
            onClick = onCancel,
            widthMultiplier = buttonsState[cancelButtonWidthMultiplier],
            modifier = Modifier
                .zIndex(buttonsState[cancelButtonZIndex])
                .drawOpacity(buttonsState[cancelButtonAlpha])
                .gravity(Alignment.Center)
        )

        BackupButton(
            onClick = onBackup,
            modifier = Modifier
                .zIndex(buttonsState[backupButtonZIndex])
                .drawOpacity(buttonsState[backupButtonAlpha])
                .gravity(Alignment.Center)
        )
    }
}

private enum class ButtonsState {
    CreateBackup,
    Cancel,
    Ok
}

private fun BackupUi.buttonState() = when(this) {
    is BackupUi.RequestBackup -> ButtonsState.CreateBackup
    is BackupUi.BackupInProgress -> ButtonsState.Cancel
    is BackupUi.BackupCompleted -> ButtonsState.Ok
}

private val AnimateButtonsTransition = transitionDefinition<ButtonsState> {
    state(ButtonsState.CreateBackup) {
        this[backupButtonAlpha] = 1f
        this[backupButtonZIndex] = 1f

        this[cancelButtonZIndex] = 0f
        this[cancelButtonAlpha] = 0f
        this[cancelButtonWidthMultiplier] = 1f
    }
    state(ButtonsState.Cancel) {
        this[backupButtonAlpha] = 0f
        this[backupButtonZIndex] = 0f

        this[cancelButtonAlpha] = 0.6f
        this[cancelButtonZIndex] = 1f
        this[cancelButtonWidthMultiplier] = 0.9f
    }
    state(ButtonsState.Ok) {
        this[backupButtonAlpha] = 0f
        this[cancelButtonAlpha] = 0f
        this[backupButtonZIndex] = 0f
        this[cancelButtonZIndex] = 1f
    }

    transition(fromState = ButtonsState.CreateBackup, toState = ButtonsState.Cancel) {
        backupButtonAlpha using tween(delayMillis = 300, easing = LinearEasing)
        cancelButtonAlpha using tween(delayMillis = 300, easing = LinearEasing)
        cancelButtonWidthMultiplier using tween(delayMillis = 300)
    }

    transition(fromState = ButtonsState.Cancel, toState = ButtonsState.CreateBackup) {
        backupButtonAlpha using tween(easing = LinearEasing)
        cancelButtonAlpha using tween(easing = LinearEasing)
    }

}

@Preview
@Composable
private fun Preview() {
    ComposeChallengesTheme {
        RequestBackupScreen()
    }
}