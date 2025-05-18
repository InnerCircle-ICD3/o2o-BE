package com.eatngo.store.dto

import com.eatngo.store.constant.StoreEnum
import com.eatngo.store.domain.*
import com.eatngo.store.dto.extension.toDomain
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * 상점 DTO
 */
data class StoreDto(
    val storeId: Long,
    val storeOwnerId: String,
    val name: String,
    val description: String?,
    val address: AddressDto,
    val businessNumber: String,
    val contactNumber: String?,
    val imageUrl: String?,
    val businessHours: List<BusinessHourDto> = emptyList(),
    val categories: List<String> = emptyList(),
    val status: StoreEnum.StoreStatus,
    val pickupStartTime: LocalTime,
    val pickupEndTime: LocalTime,
    val pickupAvailableForTomorrow: Boolean,
    val ratingAverage: Double,
    val ratingCount: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class AddressDto(
    val roadAddress: RoadAddressDto,
    val legalAddress: LegalAddressDto?,
    val adminAddress: AdminAddressDto?,
    val latitude: Double,
    val longitude: Double
)

data class BusinessHourDto(
    val dayOfWeek: DayOfWeek,
    val openTime: LocalTime,
    val closeTime: LocalTime
)

/**
 * 도로명 주소 DTO - 도메인 모델과 동일한 구조
 */
data class RoadAddressDto(
    val fullAddress: String,
    val zoneNo: String,
    val buildingName: String? = null
)

/**
 * 법정동 주소 DTO - 도메인 모델과 동일한 구조
 */
data class LegalAddressDto(
    val fullAddress: String,
    val mainAddressNo: String? = null,
    val subAddressNo: String? = null
)

/**
 * 행정동 주소 DTO - 도메인 모델과 동일한 구조
 */
data class AdminAddressDto(
    val fullAddress: String? = null
)