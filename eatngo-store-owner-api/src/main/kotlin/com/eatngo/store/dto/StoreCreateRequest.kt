package com.eatngo.store.dto

import jakarta.validation.constraints.NotBlank

/**
 * 매장 생성 요청용 dto
 */

data class StoreCreateRequest(
    // 기본 정보
    val name: String,
    val businessNumber: String,

    // 주소 정보 (Flat 구조)
    @field:NotBlank
    val roadFullAddress: String,
    @field:NotBlank
    val roadZoneNo: String,
    val roadBuildingName: String? = null,
    val legalFullAddress: String? = null,
    val legalMainAddressNo: String? = null,
    val legalSubAddressNo: String? = null,
    val adminFullAddress: String? = null,

    // 위치 정보
    @field:NotBlank
    val latitude: Double,
    @field:NotBlank
    val longitude: Double,

    // 운영 정보
    val pickupStartTime: String,
    val pickupEndTime: String,
    val pickupAvailableForTomorrow: Boolean = false,

    // 부가 정보
    val businessHours: List<BusinessHourDto>? = null,
    val contact: String? = null,
    val description: String? = null,
    val mainImageUrl: String? = null,
    val categories: List<String> = emptyList()
)