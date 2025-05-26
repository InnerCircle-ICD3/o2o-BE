package com.eatngo.store.dto

import com.eatngo.common.constant.StoreEnum

data class StorePickUpInfoRequest(
    val pickupStartTime: String? = null,
    val pickupEndTime: String? = null,
    val pickupDay: String? = null,
) {

    init {
        pickupStartTime?.let {
            require(it.matches(Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]$"))) {
                "시작 시간은 HH:mm 형식이어야 합니다"
            }
        }

        pickupEndTime?.let {
            require(it.matches(Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]$"))) {
                "종료 시간은 HH:mm 형식이어야 합니다"
            }
        }

        pickupDay?.let {
            require(it.isNotBlank()) { "픽업 일자는 비어있을 수 없습니다" }
            require(it.uppercase() in StoreEnum.PickupDay.entries.map { it -> it.name }) {
                "유효하지 않은 픽업 요일입니다"
            }
        }
    }

    companion object {
        fun from(request: StorePickUpInfoRequest): PickUpInfoDto {
            return PickUpInfoDto(
                pickupStartTime = request.pickupStartTime?.let { java.time.LocalTime.parse(it) },
                pickupEndTime = request.pickupEndTime?.let { java.time.LocalTime.parse(it) },
                pickupDay = request.pickupDay?.let { StoreEnum.PickupDay.valueOf(it.uppercase()) }
            )
        }
    }
}