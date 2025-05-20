package com.eatngo.store.dto

/**
 * 매장 카드뷰 내용 반환을 위해 사용하는 리스폰스
 */
data class StoreResponse(
    val id: Long,
    val name: String,
    val mainImageUrl: String?,
    val status: String,
    val pickupAvailableForTomorrow: Boolean, // 내일 픽업 가능 여부
    val distance: Double?,
    val foodCategory: List<String>?,
)

/**
 * 매장 카드 클릭 시 상세정보 반환을 위해 사용하는 리스폰스
 */
data class StoreDetailResponse(
    val id: Long,
    val name: String,
    val mainImageUrl: String?,
    val contact: String,
    val description: String,
    val businessNumber: String,
    val businessHours: List<BusinessHourResponse>,
    val latitude: Double,
    val longitude: Double,
    val pickupStartTime: String,
    val pickupEndTime: String,
    val pickupAvailableForTomorrow: Boolean,
    val status: String,
    val ratingAverage: Double,
    val ratingCount: Int,
    val foodCategory: List<String>?,
    val storeCategory: List<String>,
)

data class BusinessHourResponse(
    val dayOfWeek: String,  // 요일 (MONDAY, TUESDAY, ...)
    val openTime: String,   // 영업 시작 시간 (HH:MM 형식)
    val closeTime: String,  // 영업 종료 시간 (HH:MM 형식)
)