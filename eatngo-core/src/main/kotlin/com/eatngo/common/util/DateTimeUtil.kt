package com.eatngo.common.util

import java.time.LocalDateTime
import java.time.LocalTime

object DateTimeUtil {
    /**
     * LocalDateTime을 HH:mm 형식의 문자열로 변환합니다.
     */
    fun formatTimeToString(time: LocalDateTime): String {
        val localtime = time.toLocalTime()
        return String.format("%02d:%02d", localtime.hour, localtime.minute)
    }

    /**
     * HH:mm 형식의 문자열을 LocalTime으로 변환합니다.
     */
    fun parseHHmmToLocalTime(timeString: String): LocalTime {
        val parts = timeString.split(":")
        require(parts.size == 2) { "시간 문자열은 HH:mm 형식이어야 합니다." }

        val hour = parts[0].toIntOrNull() ?: throw IllegalArgumentException("시간 부분이 유효하지 않습니다.")
        val minute = parts[1].toIntOrNull() ?: throw IllegalArgumentException("분 부분이 유효하지 않습니다.")

        require(hour in 0..23) { "시간은 0에서 23 사이여야 합니다." }
        require(minute in 0..59) { "분은 0에서 59 사이여야 합니다." }

        return LocalTime.of(hour, minute)
    }
}
