package com.eatngo.search.domain

import com.eatngo.common.type.CoordinateVO
import com.eatngo.search.dto.CoordinateResultDto

data class Coordinate(
    val latitude: Double, // 위도
    val longitude: Double, // 경도
) {
    companion object {
        fun from(
            latitude: Double,
            longitude: Double,
        ): Coordinate =
            Coordinate(
                latitude = latitude,
                longitude = longitude,
            )
    }

    fun toDto(): CoordinateResultDto =
        CoordinateResultDto(
            latitude = latitude,
            longitude = longitude,
        )

    fun toVO(): CoordinateVO = CoordinateVO.from(latitude, longitude)
}
