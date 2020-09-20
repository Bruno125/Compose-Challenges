package com.bruno.aybar.composechallenges.backup

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.bruno.aybar.composechallenges.ui.ComposeChallengesTheme

@Composable
fun RequestBackupScreen() {
    Surface {

        val state = remember { mutableStateOf<BackupUi>(BackupUi.RequestBackup("28 may 2020")) }

        Column(Modifier.fillMaxSize()) {

            BackupTitle(state.value)

            Cloud(Modifier
                .height(200.dp)
                .fillMaxWidth())

            Body(
                state = state.value,
                modifier = Modifier
                    .gravity(Alignment.CenterHorizontally)
                    .weight(1f)
            )

            BottomActionButtons(
                ui = state.value,
                modifier = Modifier
                    .padding(16.dp, 16.dp, 16.dp, 40.dp)
                    .height(80.dp)
                    .fillMaxWidth(),
                onBackup = { state.value = BackupUi.BackupInProgress(0) },
                onCancel = { state.value = BackupUi.RequestBackup("28 may 2020") },
                onOk = {  }
            )

        }
    }
}

@Composable
fun Cloud(modifier: Modifier) {
    Canvas(modifier = modifier, onDraw = {

    })
}


@Preview
@Composable
private fun Preview() {
    ComposeChallengesTheme {
        RequestBackupScreen()
    }
}