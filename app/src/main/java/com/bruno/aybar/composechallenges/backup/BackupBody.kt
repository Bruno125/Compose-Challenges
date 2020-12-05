package com.bruno.aybar.composechallenges.backup

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.bruno.aybar.composechallenges.common.AnimationStateHolder
import com.bruno.aybar.composechallenges.common.transition
import com.bruno.aybar.composechallenges.ui.typography


class BodyAnimationState: AnimationStateHolder<BodyState>(BodyState.LAST_BACKUP) {

    var cachedBackupDate: String = ""; private set
    var cachedProgress: Int = 0; private set

    fun update(ui: BackupUi) {
        updateCachedValues(ui)
        animateTo(newState = when(ui) {
            is BackupUi.RequestBackup -> BodyState.LAST_BACKUP
            is BackupUi.BackupInProgress,
            is BackupUi.BackupCompleted-> BodyState.UPLOADING
        })
    }

    private fun updateCachedValues(ui: BackupUi) {
        when(ui) {
            is BackupUi.RequestBackup -> cachedBackupDate = ui.lastBackup
            is BackupUi.BackupInProgress -> cachedProgress = ui.progress
            is BackupUi.BackupCompleted -> Unit /** do nothing **/
        }
    }
}

@Composable
fun Body(state: BackupUi, modifier: Modifier) {

    val bodyState = remember { BodyAnimationState() }

    bodyState.update(state)

    val transition = transition(
        definition = bodyTransition,
        stateHolder = bodyState
    )

    Box(modifier) {
        LastBackup(
            modifier = Modifier.align(Alignment.Center),
            date = bodyState.cachedBackupDate,
            transitionState = transition)

        UploadingHint(
            modifier = Modifier.align(Alignment.Center),
            progress = bodyState.cachedProgress,
            transitionState = transition
        )
    }
}

@Composable
fun LastBackup(modifier: Modifier, date: String, transitionState: TransitionState) {
    Column(modifier.alpha(transitionState[lastBackupAlpha]),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("last backup", style = typography.caption)
        Spacer(Modifier.height(transitionState[lastBackupHintOffset].dp))
        Text(text = date, style = typography.body1)
        Spacer(Modifier.height(transitionState[lastBackupDateOffset].dp))
    }
}


@Composable
fun UploadingHint(modifier: Modifier, progress: Int, transitionState: TransitionState) {
    Column(modifier.alpha(transitionState[uploadingAlpha]),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(transitionState[uploadingOffset].dp))
        Text("uploading file", style = typography.subtitle1)
        Text(text = "$progress%", style = typography.h1)
    }
}


private val lastBackupAlpha = FloatPropKey()
private val lastBackupHintOffset = IntPropKey()
private val lastBackupDateOffset = IntPropKey()

private val uploadingAlpha = FloatPropKey()
private val uploadingOffset = IntPropKey()

private val successAlpha = FloatPropKey()
private val successOffset = IntPropKey()

enum class BodyState {
    LAST_BACKUP, UPLOADING
}

private val bodyTransition = transitionDefinition<BodyState> {
    state(BodyState.LAST_BACKUP) {
        this[lastBackupAlpha] = 1f
        this[lastBackupHintOffset] = 0
        this[lastBackupDateOffset] = 0
        this[uploadingAlpha] = 0f
        this[uploadingOffset] = 30
        this[successAlpha] = 0f
        this[successOffset] = 0
    }
    state(BodyState.UPLOADING) {
        this[lastBackupAlpha] = 0f
        this[lastBackupHintOffset] = 15
        this[lastBackupDateOffset] = 40
        this[uploadingAlpha] = 1f
        this[uploadingOffset] = 0
        this[successAlpha] = 0f
        this[successOffset] = 0
    }

    val duration = AnimationConstants.DefaultDurationMillis

    transition(fromState = BodyState.LAST_BACKUP, toState = BodyState.UPLOADING) {
        lastBackupAlpha using tween(easing = LinearOutSlowInEasing)
        lastBackupHintOffset using tween(easing = LinearOutSlowInEasing)
        lastBackupDateOffset using tween(easing = LinearOutSlowInEasing)
        uploadingAlpha using tween(delayMillis = duration)
        uploadingOffset using tween(delayMillis = duration)
    }
    transition(fromState = BodyState.UPLOADING, toState = BodyState.LAST_BACKUP) {
        uploadingAlpha using tween()
        uploadingOffset using tween()
        lastBackupAlpha using tween(delayMillis = duration)
        lastBackupHintOffset using tween(delayMillis = duration)
        lastBackupDateOffset using tween(delayMillis = duration)
    }
}
