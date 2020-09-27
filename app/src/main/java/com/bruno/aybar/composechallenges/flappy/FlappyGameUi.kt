package com.bruno.aybar.composechallenges.flappy

sealed class FlappyGameUi {

    object NotStarted: FlappyGameUi()

    data class Playing(
        val bird: Bird,
        val obstacles: List<Obstacle> = emptyList()
    ): FlappyGameUi()

    data class Finished(val finalScore: Int): FlappyGameUi()
}

data class Bird(
    val sizeDp: Float,
    val rotation: Float,
    val verticalBias: Float
)

data class Obstacle(
    val widthDp: Int,
    val heightDp: Int,
    val verticalBias: Float
)