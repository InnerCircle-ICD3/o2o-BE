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
    val storeCategory: List<String>,
    val foodCategory: List<String> = emptyList(),
    val status: StoreEnum.StoreStatus,
    val pickupStartTime: LocalTime,
    val pickupEndTime: LocalTime,
    val pickupAvailableForTomorrow: Boolean,
    val ratingAverage: Double,
    val ratingCount: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

/**
 * 주소 관련 정보를 담은 DTO
 */
data class AddressDto(
    val roadAddress: RoadAddressDto,
    val legalAddress: LegalAddressDto?,
    val adminAddress: AdminAddressDto?,
    val coordinate: Coordinate,
)

/**
 * 영업시간 정보를 담은 DTO
 */
data class BusinessHourDto(
    val dayOfWeek: DayOfWeek,
    val openTime: LocalTime,
    val closeTime: LocalTime
)

/**
 * 도로명 주소 DTO
 */
data class RoadAddressDto(
    val fullAddress: String,
    val zoneNo: String
)

/**
 * 법정동 주소 DTO
 */
data class LegalAddressDto(
    val fullAddress: String
)

/**
 * 행정동 주소 DTO
 */
data class AdminAddressDto(
    val fullAddress: String? = null
)

/**
 *  위도, 경도 좌표 DTO
 */
data class Coordinate(
    val latitude: Double,
    val longitude: Double
)