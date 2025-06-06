package com.eatngo.search.domain

import com.eatngo.common.util.DateTimeUtil
import java.time.LocalTime

data class TimeRange(
    val openTime: String, // "HH:mm" 형식
    val closeTime: String, // "HH:mm" 형식
) {
    companion object {
        fun from(
            openTime: LocalTime,
            closeTime: LocalTime,
        ): TimeRange {
            val formattedOpenTime = DateTimeUtil.formatLocalTimeToString(openTime)
            val formattedCloseTime = DateTimeUtil.formatLocalTimeToString(closeTime)

            return TimeRange(openTime = formattedOpenTime, closeTime = formattedCloseTime)
        }
    }
}
