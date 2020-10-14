package com.bruno.aybar.composechallenges.common

import androidx.compose.animation.core.AnimationClockObservable
import androidx.compose.animation.core.TransitionDefinition
import androidx.compose.animation.core.TransitionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AnimationClockAmbient

abstract class AnimationStateHolder<T>(initialState: T) {

    /**
     * Represents the current state of the animation. Its value won't change
     * until the animation to a new state has been completed.
     */
    var current by mutableStateOf(initialState)
        private set

    /**
     * Represents the state to which we are transitioning to. If there is no transition
     * occurring at the moment, then [animateTo] and [current] will have the same value.
     */
    var animatingTo by mutableStateOf(initialState)
        private set

    fun animateTo(newState: T) {
        if(newState != current) {
            animatingTo = newState
        }
    }

    fun onAnimationFinished() {
        current = animatingTo
    }

}

@Composable
fun <T> transition(
    definition: TransitionDefinition<T>,
    stateHolder: AnimationStateHolder<T>,
    clock: AnimationClockObservable = AnimationClockAmbient.current,
    label: String? = null,
    onStateChangeFinished: ((T) -> Unit)? = null
): TransitionState {
    return androidx.compose.animation.transition(
        definition = definition,
        initState = stateHolder.current,
        toState = stateHolder.animatingTo,
        clock = clock,
        label = label,
        onStateChangeFinished = {
            stateHolder.onAnimationFinished()
            onStateChangeFinished?.invoke(it)
        }
    )
}

@Composable
fun <T> transition(
    definition: TransitionDefinition<T>,
    stateHolder: AnimationSequenceStateHolder<T>,
    clock: AnimationClockObservable = AnimationClockAmbient.current,
    label: String? = null
): TransitionState {
    return androidx.compose.animation.transition(
        definition = definition,
        initState = stateHolder.current,
        toState = stateHolder.animatingTo,
        clock = clock,
        label = label,
        onStateChangeFinished = {
            stateHolder.onAnimationFinished()
            stateHolder.next()
        }
    )
}

class AnimationSequenceStateHolder<T>(private val sequence: List<T>): AnimationStateHolder<T>(
    initialState = sequence.firstOrNull() ?: throw RuntimeException("Sequence cannot be empty")
) {

    private var currentState = 0

    fun start() { next() }

    fun next() {
        if(currentState + 1 < sequence.size) {
            animateTo(sequence[++currentState])
        }
    }

}