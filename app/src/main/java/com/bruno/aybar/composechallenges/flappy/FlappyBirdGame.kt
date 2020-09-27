package com.bruno.aybar.composechallenges.flappy

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val GOING_UP = -45f
private const val FACING_DOWN = 90f

class FlappyBirdGame(
    private val birdSize: Float = 60f,
    private val birdJumpingVelocity: Float = 2.8f,
    private val frameDelayMillis: Long = 60
) {

    private var time = 0f
    private var initialY = 0f
    private var currentBias = 0f
    private var currentRotation = 0f

    private var boundsWidth = 0f
    private var boundsHeight = 0f

    private val bird = Object(width = birdSize, height = birdSize, centerX = 0.5f, centerY = 0f)
    private val obstacles = listOf<Object>()

    fun setBounds(widthDp: Float, heightDp: Float) {
        boundsWidth = widthDp
        boundsHeight = heightDp
    }

    suspend fun start(): Flow<FlappyGameUi> {
        jump(initialY = 0f)
        return flow {
            if(boundsHaveNotBeenSet()) {
                emit(FlappyGameUi.NotStarted)
                return@flow
            }
            while(true) {
                delay(frameDelayMillis)
                move()
                if(thereAreAnyCollisions() || birdIsOutOfBounds()) {
                    emit(FlappyGameUi.Finished(1))
                    break
                } else {
                    emit(buildCurrentState())
                }
            }
        }
    }

    fun jump(initialY: Float = currentBias) {
        this.time = 0f
        this.initialY = initialY
        this.currentRotation = GOING_UP
    }

    private fun move() {
        time += 0.05f
        moveBird()
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
        obstacles = emptyList()
    )

    /**
     * Evaluates current centerY position, and returns a vertical bias (value between -1 and 1),
     * relative to the current bounds.
     */
    private fun Object.getVerticalBias(): Float {
        return (2 * centerY / boundsHeight) - 1
    }

    /**
     * Evaluates current centerX position, and returns a vertical bias (value between -1 and 1),
     * relative to the current bounds.
     */
    private fun Object.getHorizontalBias(): Float {
        return (2 * centerX / boundsWidth) - 1
    }

    private fun Float.asAbsoluteY(): Float {
        return boundsHeight * (this + 1) / 2
    }

}

private data class Object(
    val width: Float,
    val height: Float,
    var centerX: Float,
    var centerY: Float
) {

    infix fun collidesWith(other: Object): Boolean {
        return false // TODO
    }

}