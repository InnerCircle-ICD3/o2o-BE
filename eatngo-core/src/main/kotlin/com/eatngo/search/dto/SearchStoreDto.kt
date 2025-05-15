package com.eatngo.search.dto

import com.eatngo.common.type.Point


data class SearchStoreDto(
    val viewPoint: Point,               // 검색하는 유저의 위치 정보
    val filter: SearchFilter? = null,   // 검색필터 -> 위치 기반 검색에서는 MVP 에서 필터링 하지 않기 때문에 null
)

data class SearchFilter (
    val category: String,   // 매장 카테고리
    val time: Long,         // 픽업 가능 시간
    val isOpen: Boolean,    // 오픈 여부
)