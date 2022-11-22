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
import com.bruno.aybar.composechallenges.ui.typography


class BodyAnimationState {

    var state: BodyState = BodyState.LAST_BACKUP; private set
    var cachedBackupDate: String = ""; private set
    var cachedProgress: Int = 0; private set

    fun update(ui: BackupUi) {
        updateCachedValues(ui)
        state = when(ui) {
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

    val properties = buildUiProperties(bodyState.state)

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

@Composable
private fun buildUiProperties(state: BodyState): BackupBodyUiProperties {
    val transition: Transition<BodyState> = updateTransition(state)
    val duration = AnimationConstants.DefaultDurationMillis

    val lastBackupAlpha: Float by transition.animateFloat(
        transitionSpec = {
            when {
                BodyState.LAST_BACKUP isTransitioningTo BodyState.UPLOADING -> tween(easing = LinearOutSlowInEasing)
                else -> tween(delayMillis = duration)
            }
        },
        targetValueByState = {
            when(it) {
                BodyState.LAST_BACKUP -> 1f
                BodyState.UPLOADING -> 0f
            }
        }, label = "lastBackupAlpha"
    )
    val lastBackupHintOffset: Int by transition.animateInt(
        transitionSpec = {
            when {
                BodyState.LAST_BACKUP isTransitioningTo BodyState.UPLOADING -> tween(easing = LinearOutSlowInEasing)
                else -> tween(delayMillis = duration)
            }
        },
        targetValueByState = {
            when(it) {
                BodyState.LAST_BACKUP -> 0
                BodyState.UPLOADING -> 15
            }
        }, label = "lastBackupHintOffset"
    )
    val lastBackupDateOffset: Int by transition.animateInt(
        transitionSpec = {
            when {
                BodyState.LAST_BACKUP isTransitioningTo BodyState.UPLOADING -> tween(easing = LinearOutSlowInEasing)
                else -> tween(delayMillis = duration)
            }
        },
        targetValueByState = {
            when(it) {
                BodyState.LAST_BACKUP -> 0
                BodyState.UPLOADING -> 40
            }
        }, label = "lastBackupDateOffset"
    )
    val uploadingAlpha: Float by transition.animateFloat(
        transitionSpec = {
            when {
                BodyState.LAST_BACKUP isTransitioningTo BodyState.UPLOADING -> tween(delayMillis = duration)
                else -> tween()
            }
        },
        targetValueByState = {
            when(it) {
                BodyState.LAST_BACKUP -> 0f
                BodyState.UPLOADING -> 1f
            }
        }, label = "uploadingAlpha"
    )
    val uploadingOffset: Int by transition.animateInt(
        transitionSpec = {
            when {
                BodyState.LAST_BACKUP isTransitioningTo BodyState.UPLOADING -> tween(delayMillis = duration)
                else -> tween()
            }
        },
        targetValueByState = {
            when(it) {
                BodyState.LAST_BACKUP -> 30
                BodyState.UPLOADING -> 0
            }
        }, label = "uploadingOffset"
    )
    return BackupBodyUiProperties(
        lastBackupAlpha = lastBackupAlpha,
        lastBackupHintOffset = lastBackupHintOffset,
        lastBackupDateOffset = lastBackupDateOffset,
        uploadingAlpha = uploadingAlpha,
        uploadingOffset = uploadingOffset,
    )
}