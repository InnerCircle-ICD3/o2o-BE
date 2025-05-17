package com.eatngo.store.dto
import java.time.ZonedDateTime

/**
 * 상점 검색 요청 DTO
 */
data class StoreSearchDto(
    val keyword: String? = null,
    val location: LocationDto? = null,
    val radius: Double? = null,
    val category: String? = null,
    val onlyOpen: Boolean? = null,        // 영업중인 매장만 필터링 (status가 OPEN인 매장)
    val availableForPickup: Boolean? = null, // 현재 픽업 가능한 매장만 필터링
    val availableForTomorrow: Boolean? = null, // 내일 픽업 가능한 매장만 필터링
    val targetDate: ZonedDateTime? = null, // 특정 날짜에 픽업 가능한 매장 필터링
    val limit: Int = 10,
    val offset: Int = 0
)
