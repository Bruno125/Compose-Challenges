package com.bruno.aybar.composechallenges.flappy

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

private const val GOING_UP = -45f
private const val FACING_DOWN = 90f
private const val CENTER = 0f
private const val OBSTACLE_WIDTH = 80f
private const val MAX_OBSTACLE_HEIGHT = 200
private const val MIN_OBSTACLE_HEIGHT = 80

class FlappyBirdGame(
    private val birdSize: Float = 48f,
    private val birdJumpingVelocity: Float = 2.8f,
    private val frameDelayMillis: Long = 60
) {

    private var time = 0f
    private var initialY = 0f
    private var currentBias = 0f
    private var currentRotation = 0f
    private var currentScore = 0

    private var boundsWidth = 0f
    private var boundsHeight = 0f

    private val bird = Object(width = birdSize, height = birdSize, centerX = 0f, centerY = 0f)
    private var obstacles = listOf<Object>()

    fun setBounds(widthDp: Float, heightDp: Float) {
        boundsWidth = widthDp
        boundsHeight = heightDp
    }

    suspend fun start(): Flow<FlappyGameUi> {
        initGameValues()
        return flow {
            if(boundsHaveNotBeenSet()) {
                emit(FlappyGameUi.NotStarted)
                return@flow
            }
            while(true) {
                if(thereAreAnyCollisions() || birdIsOutOfBounds()) {
                    emit(FlappyGameUi.Finished(1))
                    break
                } else {
                    emit(buildCurrentState())
                }
                delay(frameDelayMillis)
                move()
            }
        }
    }

    fun jump() {
        this.time = 0f
        this.initialY = currentBias
        this.currentRotation = GOING_UP
    }

    private fun initGameValues() {
        this.time = 0f
        this.initialY = CENTER
        this.currentRotation = GOING_UP
        this.currentScore = 0

        this.bird.centerX = boundsWidth / 2
        // This logic could be much more complicated
        this.obstacles = (0..6).map {
            val width = OBSTACLE_WIDTH
            val height = Random.nextInt(MIN_OBSTACLE_HEIGHT/10, MAX_OBSTACLE_HEIGHT/10) * 10f
            val up = Random.nextBoolean()
            val centerX = boundsWidth + OBSTACLE_WIDTH + OBSTACLE_WIDTH * 1.5f * it
            val centerY = if(up) height / 2f else boundsHeight - height / 2f
            Object(width, height, centerX, centerY)
        }
    }

    private fun move() {
        time += 0.05f
        moveBird()
        moveObstacles()
    }

    private fun moveBird() {
        // Formula: -GRAVITY * timeˆ2 / 2 + velocity*time
        val movement = -4.9f * time * time + birdJumpingVelocity * time
        val newBias = initialY - movement

        currentRotation = if(newBias < currentBias) { // is going up
            GOING_UP
        } else {
            (currentRotation + time * 30).coerceAtMost(FACING_DOWN)
        }
        currentBias = newBias
        bird.centerY = newBias.asAbsoluteY()
    }

    private fun moveObstacles() {
        val lastX = obstacles.maxOf { it.centerX }
        val middleScreen = boundsWidth / 2
        obstacles.forEach {
            val newCenterX = it.centerX - OBSTACLE_WIDTH / 10f
            if(it.centerX > middleScreen && newCenterX < middleScreen) {
                currentScore += 1
            }
            it.centerX = newCenterX
            val isOutside = (it.centerX + it.width / 2f) < 0f
            if(isOutside) {
                it.centerX = lastX + OBSTACLE_WIDTH * 1.5f
            }
        }
    }

    private fun thereAreAnyCollisions(): Boolean {
        return obstacles.any { obstacle -> obstacle collidesWith bird }
    }

    private fun birdIsOutOfBounds(): Boolean {
        return (bird.centerY + birdSize / 2) > boundsHeight
    }

    private fun boundsHaveNotBeenSet(): Boolean {
        return boundsWidth == 0f && boundsHeight == 0f
    }

    private fun buildCurrentState() = FlappyGameUi.Playing(
        bird = Bird(
            sizeDp = bird.width,
            verticalBias = currentBias,
            rotation = currentRotation,
        ),
        obstacles = obstacles
            .filter { it.isVisible() }
            .map {
                Obstacle(
                    widthDp = it.width,
                    heightDp = it.height,
                    topMargin = it.centerY - it.height / 2f,
                    leftMargin = it.centerX - it.width / 2f,
                    orientation = if(it.centerY > boundsHeight / 2) ObstaclePosition.Down else ObstaclePosition.Up
                )
            },
        score = currentScore
    )

    private fun Float.asAbsoluteY(): Float {
        return boundsHeight * (this + 1) / 2
    }

    private fun Object.isVisible(): Boolean {
        return (centerX + width / 2f) > 0f
    }
}

private data class Object(
    val width: Float,
    val height: Float,
    var centerX: Float,
    var centerY: Float
) {

    infix fun collidesWith(other: Object): Boolean {
        val tolerance = 20
        if (right + tolerance <= other.left || other.right - tolerance <= left)
            return false
        if (bottom - tolerance <= other.top || other.bottom + tolerance <= top)
            return false
        return true
    }

    val left get() = centerX - width / 2f
    val right get() = centerX + width / 2f
    val top get() = centerY - height / 2f
    val bottom get() = centerY + height / 2f

}