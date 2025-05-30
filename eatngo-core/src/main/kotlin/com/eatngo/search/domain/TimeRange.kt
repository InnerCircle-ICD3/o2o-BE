package com.eatngo.search.domain

data class TimeRange(
    val openTime: String, // "HH:mm" 형식
    val closeTime: String, // "HH:mm" 형식
)

enum class DayOfWeek { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY, }
