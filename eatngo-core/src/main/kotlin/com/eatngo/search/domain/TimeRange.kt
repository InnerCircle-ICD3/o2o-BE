package com.eatngo.search.domain

import com.eatngo.common.util.toHHmmString
import java.time.LocalTime

data class TimeRange(
    val openTime: String, // "HH:mm" 형식
    val closeTime: String, // "HH:mm" 형식
) {
    companion object {
        fun from(
            openTime: LocalTime,
            closeTime: LocalTime,
        ): TimeRange = TimeRange(openTime = openTime.toHHmmString(), closeTime = closeTime.toHHmmString())
    }
}
