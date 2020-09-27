package com.bruno.aybar.composechallenges.common

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.roundToInt

/**
 * Represents a positioning of a point inside a 2D box.
 * The coordinate space of the 2D box is the continuous [-1f, 1f] range in both dimensions,
 * and (verticalBias, horizontalBias) will be points in this space. (verticalBias=0f,
 * horizontalBias=0f) represents the center of the box, (verticalBias=-1f, horizontalBias=1f)
 * will be the top end, etc.
 */
data class DirectionalAlignment(
    val verticalBias: Float,
    val horizontalBias: Float
) : Alignment {
    override fun align(
        size: IntSize,
        layoutDirection: LayoutDirection
    ): IntOffset {
        // Convert to Px first and only round at the end, to avoid rounding twice while calculating
        // the new positions
        val centerX = size.width.toFloat() / 2f
        val centerY = size.height.toFloat() / 2f
        val resolvedHorizontalBias = if (layoutDirection == LayoutDirection.Ltr) {
            horizontalBias
        } else {
            -1 * horizontalBias
        }

        val x = centerX * (1 + resolvedHorizontalBias)
        val y = centerY * (1 + verticalBias)
        return IntOffset(x.roundToInt(), y.roundToInt())
    }

    @Immutable
    data class Vertical(private val bias: Float) : Alignment.Vertical {
        override fun align(size: Int): Int {
            // Convert to Px first and only round at the end, to avoid rounding twice while
            // calculating the new positions
            val center = size.toFloat() / 2f
            return (center * (1 + bias)).roundToInt()
        }
    }

    @Immutable
    data class Horizontal(private val bias: Float) : Alignment.Horizontal {
        override fun align(size: Int, layoutDirection: LayoutDirection): Int {
            // Convert to Px first and only round at the end, to avoid rounding twice while
            // calculating the new positions
            val center = size.toFloat() / 2f
            val resolvedBias = if (layoutDirection == LayoutDirection.Ltr) bias else -1 * bias
            return (center * (1 + resolvedBias)).roundToInt()
        }
    }
}