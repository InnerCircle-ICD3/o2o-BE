package com.eatngo.store.dto

import com.eatngo.common.constant.StoreEnum
import java.time.LocalTime

data class StorePickUpInfoRequest(
    val pickupStartTime: String? = null,
    val pickupEndTime: String? = null,
    val pickupDay: String? = null,
) {
    companion object {
        fun from(request: StorePickUpInfoRequest): PickUpInfoDto {
            fun parseTimeOrThrow(time: String?, field: String): LocalTime? =
                time?.runCatching { LocalTime.parse(this) }
                    ?.getOrElse { throw IllegalArgumentException("$field 는 HH:mm 형식이어야 합니다") }

            val startTime = parseTimeOrThrow(request.pickupStartTime, "시작 시간")
            val endTime = parseTimeOrThrow(request.pickupEndTime, "종료 시간")

            val pickupDay = request.pickupDay?.takeIf { it.isNotBlank() }?.uppercase()
                ?.let {
                    StoreEnum.PickupDay.entries.firstOrNull { entry -> entry.name == it }
                        ?: throw IllegalArgumentException("유효하지 않은 픽업 요일입니다")
                }

            return PickUpInfoDto(
                pickupStartTime = startTime,
                pickupEndTime = endTime,
                pickupDay = pickupDay
            )
        }
    }
}