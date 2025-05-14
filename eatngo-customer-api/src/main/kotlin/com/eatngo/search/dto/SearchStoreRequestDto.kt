package com.eatngo.search.dto

data class SearchStoreRequestDto (
    val lat: Double,            // 검색하는 유저의 위치 정보(위도)
    val lng: Double,            // 검색하는 유저의 위치 정보(경도)
    val filter: SearchFilter,   // 검색필터
    val offset: Int = 0,        // 검색 시작 위치 // TODO: (storeId 아니면 위치??)
)

data class SearchFilter (
    val category: String,
    val time: Long,
    val isOpen: Boolean,
)