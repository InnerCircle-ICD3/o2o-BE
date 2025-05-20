package com.eatngo.store.dto

import java.time.LocalTime

/**
 * 매장 수정 요청용 dto
 */

data class StoreUpdateRequest(
    val name: String? = null,
    val businessNumber: String? = null,

    // 주소 정보 (flat)
    val roadFullAddress: String? = null,
    val roadZoneNo: String? = null,
    val roadBuildingName: String? = null,
    val legalFullAddress: String? = null,
    val legalMainAddressNo: String? = null,
    val legalSubAddressNo: String? = null,
    val adminFullAddress: String? = null,

    // 위치 정보
    val latitude: Double? = null,
    val longitude: Double? = null,

    // 운영 정보
    val pickupStartTime: LocalTime? = null,
    val pickupEndTime: LocalTime? = null,
    val pickupAvailableForTomorrow: Boolean? = null,

    // 부가 정보
    val businessHours: List<BusinessHourDto>? = null,
    val contact: String? = null,
    val description: String? = null,
    val mainImageUrl: String? = null,
    val storeCategory: List<String>? = null,
    val foodCategory: List<String>? = null
)