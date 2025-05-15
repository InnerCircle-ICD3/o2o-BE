package com.eatngo.search.dto

data class SearchStoreRequestDto (
    val lat: Double,            // 검색하는 유저의 위치 정보(위도)
    val lng: Double,            // 검색하는 유저의 위치 정보(경도)
    val filter: SearchFilter?,  // 검색필터
    val offset: Int = 0,        // 검색 시작 위치 // TODO: (storeId 아니면 위치??)
)

data class SearchFilter (
    val category: String? = "", // 카테고리
    val time:   Long? = null,     // 픽업 가능 시간(해당 시간 이후)
    val status: StoreStatus = StoreStatus.ALL,    // 매장 상태
)