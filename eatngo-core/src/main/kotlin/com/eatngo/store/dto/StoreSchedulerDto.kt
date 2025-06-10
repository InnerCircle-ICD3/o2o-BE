package com.eatngo.store.dto

import com.eatngo.common.constant.StoreEnum
import java.time.DayOfWeek
import java.time.LocalTime

/**
 * 스케줄러 전용 Store DTO (주소 정보 제외)
 */
data class StoreSchedulerDto(
    val id: Long,
    val businessHours: List<BusinessHourDto>,
    val status: StoreEnum.StoreStatus
) {
    /**
     * 지정된 시간 기준으로 픽업 종료 시간이 지났는지 확인
     */
    fun isPickupTimeEnded(targetDay: DayOfWeek, targetTime: LocalTime): Boolean {
        val todayHour = businessHours.find { it.dayOfWeek == targetDay } ?: return false
        return targetTime.isAfter(todayHour.closeTime)
    }
} 