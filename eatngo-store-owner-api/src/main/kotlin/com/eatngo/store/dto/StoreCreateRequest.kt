package com.eatngo.store.dto

import java.time.LocalTime

/**
 * 매장 생성 요청용 dto
 */

data class StoreCreateRequest(
    // 기본 정보
    val name: String,
    val businessNumber: String,

    // 주소 정보 (Flat 구조)
    val roadFullAddress: String,
    val roadZoneNo: String,
    val roadBuildingName: String? = null,
    val legalFullAddress: String? = null,
    val legalMainAddressNo: String? = null,
    val legalSubAddressNo: String? = null,
    val adminFullAddress: String? = null,

    // 위치 정보
    val latitude: Double,
    val longitude: Double,

    // 운영 정보
    val pickupStartTime: LocalTime,
    val pickupEndTime: LocalTime,
    val pickupAvailableForTomorrow: Boolean = false,

    // 부가 정보
    val businessHours: List<BusinessHourDto>? = null,
    val contact: String? = null,
    val description: String? = null,
    val mainImageUrl: String? = null,
    val storeCategory: List<String> = emptyList(),
    val foodCategory: List<String> = emptyList()
)