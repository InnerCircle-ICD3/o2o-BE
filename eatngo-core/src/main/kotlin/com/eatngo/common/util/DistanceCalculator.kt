package com.eatngo.common.util

import com.eatngo.common.type.Point
import kotlin.math.*

object DistanceCalculator {

    /**
     * from 부터 to 까지의 거리를 계산합니다. (단위: km)
     */
    fun calculateDistance(from: Point, to: Point): Double {
        val earthRadius = 6371.0 // km

        val latDistance = Math.toRadians(to.lat - from.lat)
        val lonDistance = Math.toRadians(to.lng - from.lng)

        val a = sin(latDistance / 2).pow(2.0) +
                cos(Math.toRadians(from.lat)) *
                cos(Math.toRadians(to.lat)) *
                sin(lonDistance / 2).pow(2.0)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return (earthRadius * c * 10.0).roundToInt() / 10.0  // 소수점 첫째 자리 반올림
    }
}