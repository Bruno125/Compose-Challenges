package com.bruno.aybar.composechallenges.common

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.roundToInt


/**
 * Represents an absolute positioning of a point inside a 2D box. The position will not be
 * automatically mirrored in Rtl context.
 */
@Immutable
data class AbsoluteAlignment(
    private val verticalBias: Float = 0f,
    private val horizontalBias: Float = 0f
) : Alignment {
    /**
     * Returns the position of a 2D point in a container of a given size, according to this
     * [AbsoluteAlignment]. The position will not be mirrored in Rtl context.
     */
    override fun align(size: IntSize, layoutDirection: LayoutDirection): IntOffset {
        // Convert to Px first and only round at the end, to avoid rounding twice while calculating
        // the new positions
        val centerX = size.width.toFloat() / 2f
        val centerY = size.height.toFloat() / 2f

        val x = centerX * (1 + horizontalBias)
        val y = centerY * (1 + verticalBias)
        return IntOffset(x.roundToInt(), y.roundToInt())
    }

}
