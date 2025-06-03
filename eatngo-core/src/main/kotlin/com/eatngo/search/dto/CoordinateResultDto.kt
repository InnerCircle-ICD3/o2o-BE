package com.eatngo.search.dto

import com.eatngo.common.type.CoordinateVO

data class CoordinateResultDto(
    val longitude: Double,
    val latitude: Double,
) {
    companion object {
        fun from(coordinate: CoordinateVO): CoordinateResultDto =
            CoordinateResultDto(
                longitude = coordinate.longitude,
                latitude = coordinate.latitude,
            )

        fun from(
            latitude: Double,
            longitude: Double,
        ): CoordinateResultDto =
            CoordinateResultDto(
                longitude = longitude,
                latitude = latitude,
            )
    }
}
