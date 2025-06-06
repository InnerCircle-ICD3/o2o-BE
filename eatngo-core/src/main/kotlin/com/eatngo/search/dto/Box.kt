package com.eatngo.search.dto

import com.eatngo.common.type.CoordinateVO

data class Box(
    val topLeft: CoordinateVO,
    val bottomRight: CoordinateVO,
) {
    companion object {
        fun from(
            topLeft: CoordinateVO,
            bottomRight: CoordinateVO,
        ): Box =
            Box(
                topLeft = topLeft,
                bottomRight = bottomRight,
            )
    }
}
