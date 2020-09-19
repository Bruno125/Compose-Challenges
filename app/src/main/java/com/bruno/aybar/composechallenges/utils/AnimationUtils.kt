package com.bruno.aybar.composechallenges.utils

import androidx.compose.animation.core.*

fun <T,V: AnimationVector> BaseAnimatedValue<T, V>.animationSequence(
    targetValues: List<T>,
    anim: AnimationSpec<T> = TweenSpec(easing = LinearEasing)
) {
    if (targetValues.isEmpty()) return

    fun animateAndContinue(index: Int) {
        val target = targetValues[index]
        animateTo(target, anim, onEnd = { reason, _ ->
            val hasNext = (index + 1) < targetValues.size
            if(reason == AnimationEndReason.TargetReached && hasNext) {
                animateAndContinue(index + 1)
            }
        })
    }

    animateAndContinue(0)
}