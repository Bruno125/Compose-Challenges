package com.bruno.aybar.composechallenges.backup

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.bruno.aybar.composechallenges.common.AnimationStateHolder
import com.bruno.aybar.composechallenges.ui.typography

private const val ANIM_DURATION = AnimationConstants.DefaultDurationMillis

private class BackupBodyUiProperties(
    val lastBackupAlpha: Float,
    val lastBackupHintOffset: Int,
    val lastBackupDateOffset: Int,

    val uploadingAlpha: Float,
    val uploadingOffset: Int,
)

enum class BodyState {
    LAST_BACKUP, UPLOADING
}

class BodyAnimationState: AnimationStateHolder<BodyState>(BodyState.LAST_BACKUP) {

    var cachedBackupDate: String = ""; private set
    var cachedProgress: Int = 0; private set

    fun update(ui: BackupUi) {
        updateCachedValues(ui)
        current = when(ui) {
            is BackupUi.RequestBackup -> BodyState.LAST_BACKUP
            is BackupUi.BackupInProgress,
            is BackupUi.BackupCompleted-> BodyState.UPLOADING
        }
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
fun BackupBody(state: BackupUi, modifier: Modifier) {

    val bodyState = remember { BodyAnimationState() }
    bodyState.update(state)

    val transition: Transition<BodyState> = updateTransition(bodyState.current)

    val lastBackupAlpha: Float by transition.animateFloat(
        transitionSpec = {
            when {
                BodyState.LAST_BACKUP isTransitioningTo BodyState.UPLOADING -> tween(easing = LinearOutSlowInEasing)
                else -> tween(delayMillis = ANIM_DURATION)
            }
        },
        targetValueByState = {
            when(it) {
                BodyState.LAST_BACKUP -> 1f
                BodyState.UPLOADING -> 0f
            }
        }
    )
    val lastBackupHintOffset: Int by transition.animateInt(
        transitionSpec = {
            when {
                BodyState.LAST_BACKUP isTransitioningTo BodyState.UPLOADING -> tween(easing = LinearOutSlowInEasing)
                else -> tween(delayMillis = ANIM_DURATION)
            }
        },
        targetValueByState = {
            when(it) {
                BodyState.LAST_BACKUP -> 0
                BodyState.UPLOADING -> 15
            }
        }
    )
    val lastBackupDateOffset: Int by transition.animateInt(
        transitionSpec = {
            when {
                BodyState.LAST_BACKUP isTransitioningTo BodyState.UPLOADING -> tween(easing = LinearOutSlowInEasing)
                else -> tween(delayMillis = ANIM_DURATION)
            }
        },
        targetValueByState = {
            when(it) {
                BodyState.LAST_BACKUP -> 0
                BodyState.UPLOADING -> 40
            }
        }
    )

    val uploadingAlpha: Float by transition.animateFloat(
        transitionSpec = {
            when {
                BodyState.LAST_BACKUP isTransitioningTo BodyState.UPLOADING -> tween(delayMillis = ANIM_DURATION)
                else -> tween()
            }
        },
        targetValueByState = {
            when(it) {
                BodyState.LAST_BACKUP -> 0f
                BodyState.UPLOADING -> 1f
            }
        }
    )
    val uploadingOffset: Int by transition.animateInt(
        transitionSpec = {
            when {
                BodyState.LAST_BACKUP isTransitioningTo BodyState.UPLOADING -> tween(delayMillis = ANIM_DURATION)
                else -> tween()
            }
        },
        targetValueByState = {
            when(it) {
                BodyState.LAST_BACKUP -> 30
                BodyState.UPLOADING -> 0
            }
        }
    )

    val properties = BackupBodyUiProperties(
        lastBackupAlpha = lastBackupAlpha,
        lastBackupHintOffset = lastBackupHintOffset,
        lastBackupDateOffset = lastBackupDateOffset,
        uploadingAlpha = uploadingAlpha,
        uploadingOffset = uploadingOffset,
    )

    Box(modifier) {
        LastBackup(
            modifier = Modifier.align(Alignment.Center),
            date = bodyState.cachedBackupDate,
            properties = properties,
        )

        UploadingHint(
            modifier = Modifier.align(Alignment.Center),
            progress = bodyState.cachedProgress,
            properties = properties,
        )
    }
}

@Composable
private fun LastBackup(modifier: Modifier, date: String, properties: BackupBodyUiProperties) {
    Column(modifier.alpha(properties.lastBackupAlpha),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("last backup", style = typography.caption)
        Spacer(Modifier.height(properties.lastBackupHintOffset.dp))
        Text(text = date, style = typography.body1)
        Spacer(Modifier.height(properties.lastBackupDateOffset.dp))
    }
}


@Composable
private fun UploadingHint(modifier: Modifier, progress: Int, properties: BackupBodyUiProperties) {
    Column(modifier.alpha(properties.uploadingAlpha),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(properties.uploadingOffset.dp))
        Text("uploading file", style = typography.subtitle1)
        Text(text = "$progress%", style = typography.h1)
    }
}