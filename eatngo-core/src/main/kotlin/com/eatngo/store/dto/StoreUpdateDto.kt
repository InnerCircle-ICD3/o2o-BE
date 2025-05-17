package com.eatngo.store.dto

import com.eatngo.store.constant.StoreEnum
import com.eatngo.store.domain.*
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


/**
 * 상점 수정 요청 DTO
 */
data class StoreUpdateDto(
    val name: String? = null,
    val address: AddressDto,
    val businessNumber: String? = null,
    val businessHours: List<BusinessHourDto>? = null,
    val contactNumber: String? = null,
    val description: String? = null,
    val pickupStartTime: LocalTime? = null,
    val pickupEndTime: LocalTime? = null,
    val pickupAvailableForTomorrow: Boolean? = null,
    val mainImageUrl: String? = null,
    val categories: List<String>? = null
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