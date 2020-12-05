package com.bruno.aybar.composechallenges.backup

import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.IntPropKey
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.transition
import androidx.compose.material.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.bruno.aybar.composechallenges.ui.typography

private val titleAlpha = FloatPropKey()
private val titleOffset = IntPropKey()

enum class BackupTitleState {
    VISIBLE, HIDDEN
}

@Composable
fun BackupTitle(ui: BackupUi) {
    val transitionState = transition(
        definition = titleAnimation,
        toState = when(ui) {
            is BackupUi.RequestBackup -> BackupTitleState.VISIBLE
            else -> BackupTitleState.HIDDEN
        }
    )

    Text(
        text = "Cloud Storage",
        style = typography.subtitle1,
        modifier = Modifier
            .alpha(transitionState[titleAlpha])
            .padding(
                start = 16.dp, end = 16.dp,
                bottom = 32.dp - transitionState[titleOffset].dp,
                top = transitionState[titleOffset].dp
            )
    )
}

private val titleAnimation = transitionDefinition<BackupTitleState> {
    state(BackupTitleState.VISIBLE) {
        this[titleAlpha] = 1f
        this[titleOffset] = 16
    }
    state(BackupTitleState.HIDDEN) {
        this[titleAlpha] = 0f
        this[titleOffset] = 0
    }
}
