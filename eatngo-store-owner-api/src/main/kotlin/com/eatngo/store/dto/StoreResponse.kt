package com.eatngo.store.dto

/**
 * 점주가 매장을 관리할 때 상세정보 반환을 위해 사용하는 리스폰스
 * TODO: 추후 백오피스 UI 확정되면 수정 가능성 있음
 */
data class StoreDetailResponse(
    val id: Long,
    val name: String,
    val mainImageUrl: String?,
    val contact: String,
    val description: String,
    val businessNumber: String,
    val businessHour: List<String>,
    val latitude: Double,
    val longitude: Double,
    val pickupStartTime: String,
    val pickupEndTime: String,
    val pickupAvailableForTomorrow: Boolean,
    val status: String,
    val ratingAverage: Double,
    val ratingCount: Int,
    val foodCategory: List<String>?,
    val storeCategory: List<String>
)
