package com.eatngo.search.dto

import java.time.LocalDateTime

data class SearchStoreRequestDto (
    val lat: Double,            // 검색하는 유저의 위치 정보(위도)
    val lng: Double,            // 검색하는 유저의 위치 정보(경도)
    val filter: SearchFilter?,  // 검색필터
    val offset: Int = 0,        // 검색 시작 위치 // TODO: (storeId 아니면 위치??)
)

data class SearchFilter (
    val category: String?,          // 카테고리
    val time: LocalDateTime?,     // 픽업 가능 시간(해당 시간 이후)
    val searchText: String?,        // 검색어(대상 필드: 매장명, 음식명, 카테고리)
    val status: StoreStatus = StoreStatus.ALL,    // 매장 상태
)