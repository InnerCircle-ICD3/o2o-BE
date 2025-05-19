package com.eatngo.store.dto

import com.eatngo.common.constant.StoreEnum
import java.time.LocalTime


/**
 * 상점 수정 요청 DTO
 */
data class StoreUpdateDto(
    val name: String,
    val address: AddressDto,
    val businessNumber: String,
    val businessHours: List<BusinessHourDto>? = null,
    val contactNumber: String? = null,
    val description: String? = null,
    val pickupStartTime: LocalTime,
    val pickupEndTime: LocalTime,
    val pickupAvailableForTomorrow: Boolean,
    val mainImageUrl: String? = null,
    val storeCategory: List<String>,
    val foodCategory: List<String> = emptyList(),
)

/**
 * 상점 상태 변경 요청 DTO
 */
data class StatusUpdateRequest(
    val status: StoreEnum.StoreStatus
)

/**
 * 상점 픽업 정보 변경 요청 DTO
 */
data class PickupInfoUpdateRequest(
    val pickupStartTime: String,  // 픽업 시작 시간 (HH:mm)
    val pickupEndTime: String,    // 픽업 종료 시간 (HH:mm)
    val pickupAvailableForTomorrow: Boolean = false // 내일 픽업 가능 여부
)