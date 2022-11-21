package com.bruno.aybar.composechallenges.flappy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.max

class FlappyBirdViewModel: ViewModel() {

    private var _state = MutableLiveData<FlappyGameUi>(FlappyGameUi.NotStarted)
    val state: LiveData<FlappyGameUi> = _state

    private var _scoreBoard = MutableLiveData(Scoreboard(0,0))
    val scoreBoard: LiveData<Scoreboard> = _scoreBoard

    private var hasStarted = false
    private var bestScore = 0
    private var game = FlappyBirdGame()

    fun onGameBoundsSet(widthDp: Float, heightDp: Float) {
        game.setBounds(widthDp, heightDp)
    }

    fun onTap() {
        if(hasStarted) {
            game.jump()
        } else {
            startGame()
        }
    }

    private fun startGame() {
        viewModelScope.launch(Dispatchers.IO) {
            hasStarted = true
            game.start().collectLatest {
                _state.postValue(it)
                when(it) {
                    is FlappyGameUi.Playing -> updateScore(it)
                    is FlappyGameUi.Finished -> hasStarted = false
                    is FlappyGameUi.NotStarted -> Unit // do nothing
                }
            }
        }
    }

    private fun updateScore(current: FlappyGameUi.Playing) {
        bestScore = max(bestScore, current.score)

        _scoreBoard.postValue(
            Scoreboard(
                current = current.score,
                best = bestScore
            )
        )
    }

}
