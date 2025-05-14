package com.eatngo.search.dto

data class SearchStoreRequestDto (
    val viewPoint: Point,    // 검색하는 유저의 위치 정보
    val filter: SearchFilter,   // 검색필터
    val offset: Int = 0,        // 검색 시작 위치 // TODO: (storeId 아니면 위치??)
    val limit: Int = 20,        // 한 페이지에 보여줄 매장 수
)

data class SearchFilter (
    val category: String,
    val time: Long,
    val isOpen: Boolean,
)