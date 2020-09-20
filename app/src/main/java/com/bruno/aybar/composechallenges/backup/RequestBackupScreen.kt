package com.bruno.aybar.composechallenges.backup

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.bruno.aybar.composechallenges.ui.ComposeChallengesTheme
import com.bruno.aybar.composechallenges.ui.typography

@Composable
fun RequestBackupScreen() {
    Surface {

        val state = mutableStateOf<BackupUi>(BackupUi.RequestBackup("28 may 2020"))

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
                onCancel = { state.value = BackupUi.RequestBackup("28 may 2020") },
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