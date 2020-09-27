package com.bruno.aybar.composechallenges.flappy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FlappyBirdViewModel: ViewModel() {

    private var _state = MutableLiveData<FlappyGameUi>(FlappyGameUi.NotStarted)
    val state: LiveData<FlappyGameUi> = _state

    private var hasStarted = false
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
        viewModelScope.launch(Dispatchers.Default) {
            hasStarted = true
            game.start().collectLatest {
                _state.postValue(it)
                if(it is FlappyGameUi.Finished) hasStarted = false
            }
        }
    }

}
