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

            BottomActionButtons(
                state = state.value,
                modifier = Modifier.padding(16.dp)
                    .constrainAs(backupRef) {
                        bottom.linkTo(parent.bottom)
                        centerHorizontallyTo(parent)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                ,
                onBackup = { state.value = BackupUi.BackupInProgress(0f) },
                onCancel = { state.value = BackupUi.RequestBackup },
                onOk = {  })

        }
    }
}


@Preview
@Composable
private fun Preview() {
    ComposeChallengesTheme {
        RequestBackupScreen()
    }
}