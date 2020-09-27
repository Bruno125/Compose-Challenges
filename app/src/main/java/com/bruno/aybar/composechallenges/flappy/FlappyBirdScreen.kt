package com.bruno.aybar.composechallenges.flappy

import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.onActive
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.drawLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.bruno.aybar.composechallenges.R
import com.bruno.aybar.composechallenges.common.AbsoluteAlignment

val skyColor = Color(0xFF74B9F5)
val lightGreen = Color(0xFF75B6E0)
val darkGreen = Color(0xFF276C3D)
val brown = Color(0xFF755B55)

@Composable
fun FlappyBirdScreen() {
    val gameViewModel = remember { FlappyBirdViewModel() }

    Surface {
        Column(Modifier.fillMaxSize()) {

            GameArea(gameViewModel, modifier = Modifier
                .fillMaxWidth()
                .weight(4f)
            )

            Scoreboard(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            )

        }
    }
}

@Composable
fun GameArea(viewModel: FlappyBirdViewModel, modifier: Modifier) {

    val state = viewModel.state.observeAsState(FlappyGameUi.NotStarted)

    WithConstraints(modifier) {
        onActive {
            viewModel.onGameBoundsSet(maxWidth.value, maxHeight.value)
        }
        Stack(Modifier.clickable(onClick = viewModel::onTap)) {
            val currentState = state.value
            val birdAlignment = currentState.getBirdAlignment()
            Bird(currentState, Modifier.gravity(birdAlignment))
        }
    }
}

private fun FlappyGameUi.getBirdAlignment() = when(this) {
    is FlappyGameUi.NotStarted -> Alignment.Center
    is FlappyGameUi.Finished -> Alignment.BottomCenter
    is FlappyGameUi.Playing -> AbsoluteAlignment(bird.verticalBias, 0f)
}

@Composable
private fun Bird(state: FlappyGameUi, modifier: Modifier) {
    val birdRotation = when(state) {
        is FlappyGameUi.NotStarted,
        is FlappyGameUi.Finished -> 0f
        is FlappyGameUi.Playing -> state.bird.rotation
    }
    Image(
        asset = imageResource(id = R.drawable.bird),
        modifier = modifier
            .size(60.dp)
            .drawLayer(rotationZ = birdRotation)
    )
}

@Composable
fun Scoreboard(modifier: Modifier) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colors.background)
            .padding(vertical = 24.dp),
        verticalGravity = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(horizontalGravity = Alignment.CenterHorizontally) {
            Text("SCORE", style = MaterialTheme.typography.subtitle1)
            Text("2", style = MaterialTheme.typography.body1)
        }
        Column(horizontalGravity = Alignment.CenterHorizontally) {
            Text("BEST", style = MaterialTheme.typography.subtitle1)
            Text("0", style = MaterialTheme.typography.body1)
        }
    }
}

@Preview
@Composable
fun GamePreview() {
    FlappyBirdTheme {
        FlappyBirdScreen()
    }
}