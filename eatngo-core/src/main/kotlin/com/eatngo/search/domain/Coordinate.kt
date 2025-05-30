package com.eatngo.search.domain

import com.eatngo.common.type.CoordinateVO

data class Coordinate(
    val latitude: Double, // 위도
    val longitude: Double, // 경도
) {
    companion object {
        fun from(coordinateVO: CoordinateVO): Coordinate =
            Coordinate(
                latitude = coordinateVO.latitude,
                longitude = coordinateVO.longitude,
            )

        fun from(
            latitude: Double,
            longitude: Double,
        ): Coordinate =
            Coordinate(
                latitude = latitude,
                longitude = longitude,
            )
    }
}
