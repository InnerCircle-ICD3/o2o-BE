package com.eatngo.search.dto

import com.eatngo.common.type.CoordinateVO
import com.eatngo.search.constant.StoreEnum
import java.time.LocalDateTime

data class SearchStoreQueryDto(
    val viewCoordinate: CoordinateVO, // 검색하는 유저의 위치 정보
    val filter: SearchFilter? = null, // 검색필터 -> 위치 기반 검색에서는 MVP 에서 필터링 하지 않기 때문에 null
)

data class SearchFilter(
    val category: StoreEnum.StoreCategory?, // 매장 카테고리
    val time: LocalDateTime?, // 픽업 가능 시간
    val searchText: String?, // 검색어(대상 필드: 매장명, 음식명, 카테고리)
    val status: Int, // 매장 상태(0: 영업종료, 1: 영업중, 9: 전체 상태)
)
