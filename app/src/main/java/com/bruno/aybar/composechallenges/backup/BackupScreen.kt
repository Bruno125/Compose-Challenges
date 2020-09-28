package com.bruno.aybar.composechallenges.backup

import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.ui.tooling.preview.Preview
import com.bruno.aybar.composechallenges.ui.ComposeChallengesTheme

@Composable
fun BackupScreen(viewModel: BackupViewModel) {
    val state = viewModel.state.observeAsState().value ?: return

    Surface {
        Stack {
            Column(Modifier.fillMaxSize()) {
                BackupTitle(state)
                BackupCloud(
                    ui = state,
                    modifier = Modifier
                        .height(350.dp)
                        .fillMaxWidth()
                        .zIndex(2f)
                )
                Body(state, Modifier.gravity(Alignment.CenterHorizontally))
                Spacer(Modifier.weight(1f))
                BackupActionButtons(
                    ui = state,
                    modifier = Modifier
                        .padding(16.dp, 16.dp, 16.dp, 40.dp)
                        .height(80.dp)
                        .fillMaxWidth(),
                    onBackup = { viewModel.onBackup() },
                    onCancel = { viewModel.onCancel() }
                )
            }
            BackupCompleted(
                ui = state,
                modifier = Modifier.fillMaxSize().zIndex(3f),
                onOk = { viewModel.onOk() }
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ComposeChallengesTheme {
        val viewModel = remember { BackupViewModel() }
        BackupScreen(viewModel)
    }
}