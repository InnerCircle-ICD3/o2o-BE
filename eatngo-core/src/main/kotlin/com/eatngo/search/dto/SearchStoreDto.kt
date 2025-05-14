package com.eatngo.search.dto

data class SearchStoreDto(
    val viewPoint: Point,       // 검색하는 유저의 위치 정보
    val filter: SearchFilter? = null,   // 검색필터
)

// TODO : 공통적으로 사용하는 구조체 위치 조정
data class Point (
    val lat: String,
    val lng: String,
)

data class SearchFilter (
    val category: String,
    val time: Long,
    val isOpen: Boolean,
)