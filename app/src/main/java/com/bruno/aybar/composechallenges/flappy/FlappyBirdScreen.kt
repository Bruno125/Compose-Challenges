package com.bruno.aybar.composechallenges.flappy

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.onActive
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.Text
import androidx.compose.ui.graphics.graphicsLayer
import com.bruno.aybar.composechallenges.R

val skyColor = Color(0xFF74B9F5)
val lightGreen = Color(0xFF75B6E0)
val darkGreen = Color(0xFF276C3D)
val brown = Color(0xFF755B55)

private val obstacleGreenNormal = Color(0xFF75BE2F)
private val obstacleGreenDark = Color(0xFF518718)
private val obstacleGreenLight = Color(0xFF9AE456)
private val obstacleGreenExtraLight = Color(0xFFD8FF80)
private val obstacleBorder = Color(0xFF52513A)
private val obstacleBorderWidth = 3.dp

@Composable
fun FlappyBirdScreen() {
    val gameViewModel = remember { FlappyBirdViewModel() }

    Surface {
        Column(Modifier.fillMaxSize()) {

            GameArea(gameViewModel, modifier = Modifier
                .fillMaxWidth()
                .weight(4f)
            )

            Scoreboard(gameViewModel, modifier = Modifier
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
        Box(Modifier.clickable(onClick = viewModel::onTap, indication = null)) {
            val currentState = state.value
            val birdAlignment = currentState.getBirdAlignment()
            Bird(currentState, Modifier.align(birdAlignment))

            currentState.getObstacles().forEach { obstacle ->
                DrawObstacle(Modifier
                    .size(obstacle.widthDp.dp, obstacle.heightDp.dp)
                    .graphicsLayer {
                        rotationX = when(obstacle.orientation) {
                            ObstaclePosition.Up -> 180f
                            ObstaclePosition.Down -> 0f
                        }
                    }
                    .absoluteOffset(
                        x = obstacle.leftMargin.dp,
                        y = obstacle.topMargin.dp
                    )
                )
            }

            if(currentState !is FlappyGameUi.Playing) {
                Text("T A P  T O  P L A Y", Modifier.align(
                    BiasAlignment(verticalBias = -0.3f, horizontalBias = 0f)
                ))
            }
        }
    }
}

private fun FlappyGameUi.getBirdAlignment() = when(this) {
    is FlappyGameUi.NotStarted -> Alignment.Center
    is FlappyGameUi.Finished -> Alignment.BottomCenter
    is FlappyGameUi.Playing -> BiasAlignment(verticalBias = bird.verticalBias, horizontalBias = 0f)
}

private fun FlappyGameUi.getObstacles(): List<Obstacle> = when(this) {
    is FlappyGameUi.Playing -> obstacles
    else -> emptyList()
}

@Composable
private fun Bird(state: FlappyGameUi, modifier: Modifier) {
    val birdRotation = when(state) {
        is FlappyGameUi.NotStarted,
        is FlappyGameUi.Finished -> 0f
        is FlappyGameUi.Playing -> state.bird.rotation
    }
    Image(
        bitmap = imageResource(id = R.drawable.bird),
        modifier = modifier
            .size(48.dp)
            .graphicsLayer { rotationZ = birdRotation }
    )
}

@Composable
fun DrawObstacle(modifier: Modifier) {
    Box(modifier) {
        ObstacleBody(ObstacleBorderMode.Sides, modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxSize()
        )
        ObstacleBody(ObstacleBorderMode.AllBorders, modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
        )
    }
}

private enum class ObstacleBorderMode { AllBorders, Sides }

@Composable
private fun ObstacleBody(borderMode: ObstacleBorderMode, modifier: Modifier) {
    WithConstraints(modifier) {
        val canvasModifier = Modifier.fillMaxSize().composed {
            when(borderMode) {
                ObstacleBorderMode.AllBorders -> border(
                    width = obstacleBorderWidth,
                    color = obstacleBorder,
                    shape = RoundedCornerShape(4.dp)
                )
                else -> this
            }
        }

        Canvas(canvasModifier) {
            fun line(color: Color, width: Float, offset: Float) {
                drawRect(color, size = Size(width, size.height), topLeft = Offset(offset, 0f))
            }

            drawRect(color = obstacleGreenNormal)

            line(obstacleGreenLight, width = 140f, offset = 0f)
            line(obstacleGreenNormal, width = 20f, offset = 100f)
            line(obstacleGreenExtraLight, width = 20f, offset = 20f)

            line(obstacleGreenDark, width = 35f, offset = size.width - 35f)
            line(obstacleGreenDark, width = 20f, offset = size.width - 70f)

            if (borderMode == ObstacleBorderMode.Sides) {
                val borderWidth = obstacleBorderWidth.toPx()
                line(obstacleBorder, borderWidth, offset = -borderWidth)
                line(obstacleBorder, borderWidth, offset = size.width)
            }
        }
    }
}

@Composable
fun Scoreboard(gameViewModel: FlappyBirdViewModel, modifier: Modifier) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colors.background)
            .padding(vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val scoreboard = gameViewModel.scoreBoard.observeAsState().value ?: Scoreboard(0,0)

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("SCORE", style = MaterialTheme.typography.subtitle1)
            Text("${scoreboard.current}", style = MaterialTheme.typography.body1)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("BEST", style = MaterialTheme.typography.subtitle1)
            Text("${scoreboard.best}", style = MaterialTheme.typography.body1)
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