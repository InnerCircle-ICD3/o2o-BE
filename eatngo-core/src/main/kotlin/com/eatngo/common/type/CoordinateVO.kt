package com.eatngo.common.type

@JvmInline
value class CoordinateVO(val value: Pair<Double, Double>) {
    init {
        require(value.first in -90.0..90.0) { "위도는 -90에서 90 사이여야 합니다" }
        require(value.second in -180.0..180.0) { "경도는 -180에서 180 사이여야 합니다" }
    }

    val latitude: Double get() = value.first
    val longitude: Double get() = value.second

    companion object {
        fun from(latitude: Double, longitude: Double): CoordinateVO =
            CoordinateVO(Pair(latitude, longitude))
    }
}