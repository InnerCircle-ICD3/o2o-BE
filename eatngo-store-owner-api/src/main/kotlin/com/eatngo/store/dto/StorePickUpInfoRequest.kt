package com.eatngo.store.dto

import com.eatngo.common.constant.StoreEnum

data class StorePickUpInfoRequest (
    val pickupStartTime: String? = null,
    val pickupEndTime: String? = null,
    val pickupDay: String? = null,
){
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