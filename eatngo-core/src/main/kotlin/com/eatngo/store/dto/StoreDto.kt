package com.eatngo.store.dto

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.vo.*
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.LocalDateTime


/**
 * 상점 DTO
 */
data class StoreDto(
    val storeId: Long,
    val storeOwnerId: Long,
    val name: String,
    val description: String?,
    val address: AddressDto,
    val businessNumber: String,
    val contactNumber: String?,
    val imageUrl: String?,
    val businessHours: List<BusinessHourDto> = emptyList(),
    val storeCategoryInfo: StoreCategoryInfoDto,
    val status: StoreEnum.StoreStatus,
    val pickUpInfo: PickUpInfoDto,
    val reviewInfo: ReviewInfoDto,
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
    val coordinate: CoordinateDto
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
    val fullAddress: String? = null
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
data class CoordinateDto(
    val latitude: Double,
    val longitude: Double
)

/**
 * 픽업과 관련된 정보 DTO
 */
data class PickUpInfoDto(
    val pickupStartTime: LocalTime,
    val pickupEndTime: LocalTime,
    val pickupAvailableForTomorrow: Boolean = false,
)

/**
 * 리뷰 정보 DTO
 */
data class ReviewInfoDto(
    val ratingAverage: Double = 0.0,
    val ratingCount: Int = 0,
)

/**
 * 매장의 카테고리 정보(분류와 사용자 입력 카테고리) DTO
 */
data class StoreCategoryInfoDto(
    val storeCategory: List<String>,
    val foodCategory: List<String>?
)