package com.bruno.aybar.composechallenges.backup

import androidx.compose.animation.core.*
import androidx.compose.animation.transition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawOpacity
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.ui.tooling.preview.Preview
import com.bruno.aybar.composechallenges.ui.ComposeChallengesTheme
import com.bruno.aybar.composechallenges.ui.typography
import kotlin.math.tan

@Composable
fun RequestBackupScreen(viewModel: BackupViewModel) {
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
                BottomActionButtons(
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
        RequestBackupScreen(viewModel)
    }
}