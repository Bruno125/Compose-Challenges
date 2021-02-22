package com.bruno.aybar.composechallenges.backup

import androidx.compose.animation.core.*
import androidx.compose.material.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.bruno.aybar.composechallenges.ui.typography

enum class BackupTitleState {
    VISIBLE, HIDDEN
}

@Composable
fun BackupTitle(ui: BackupUi) {
    val state = when(ui) {
        is BackupUi.RequestBackup -> BackupTitleState.VISIBLE
        else -> BackupTitleState.HIDDEN
    }

    val titleAlpha: Float by animateFloatAsState(when(state) {
        BackupTitleState.VISIBLE -> 1f
        BackupTitleState.HIDDEN -> 0f
    })
    val titleOffset: Int by animateIntAsState(when(state) {
        BackupTitleState.VISIBLE -> 16
        BackupTitleState.HIDDEN -> 0
    })

    Text(
        text = "Cloud Storage",
        style = typography.subtitle1,
        modifier = Modifier
            .alpha(titleAlpha)
            .padding(
                start = 16.dp, end = 16.dp,
                bottom = 32.dp - titleOffset.dp,
                top = titleOffset.dp
            )
    )
}