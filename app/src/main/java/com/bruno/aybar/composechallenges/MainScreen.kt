package com.bruno.aybar.composechallenges

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.bruno.aybar.composechallenges.ui.ComposeChallengesTheme
import com.bruno.aybar.composechallenges.ui.typography

@Composable
fun MainScreen() {
    Surface(Modifier.fillMaxSize()) {
        Column(Modifier.padding(24.dp)) {
            Text("Cloud Storage", style = typography.subtitle1)

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Backup")
            }
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    ComposeChallengesTheme {
        MainScreen()
    }
}