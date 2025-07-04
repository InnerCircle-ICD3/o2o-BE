package com.eatngo.search.dto

import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.exception.search.SearchException
import com.eatngo.common.type.CoordinateVO
import com.eatngo.extension.orThrow

data class StoreFilterDto(
    val viewCoordinate: CoordinateVO, // 검색하는 유저의 위치 정보
    val filter: SearchFilter, // 검색 필터 (매장 카테고리, 픽업 가능 시간, 매장 상태)
) {
    companion object {
        fun from(
            latitude: Double,
            longitude: Double,
            searchText: String? = null, // 검색 키워드
            storeCategory: String?,
            time: String?,
            onlyReservable: Boolean = false, // 매장 상태 (true: 영업중, false,null: 전체)
            lastId: String? = null, // 마지막 검색 paginationToken
        ): StoreFilterDto {
            val viewCoordinate =
                CoordinateVO.from(latitude, longitude).orThrow {
                    SearchException.SearchInvalidCoordinate(latitude, longitude)
                }

            val filter =
                SearchFilter
                    .from(
                        searchText = searchText,
                        storeCategory = storeCategory,
                        time = time,
                        onlyReservable = onlyReservable,
                        lastId = lastId, // 마지막 검색 paginationToken
                    ).orThrow {
                        SearchException.SearchInvalidFilter()
                    }

            return StoreFilterDto(
                viewCoordinate = viewCoordinate,
                filter = filter,
            )
        }
    }
}

data class SearchFilter(
    val searchText: String? = null, // 검색 키워드
    val storeCategory: StoreEnum.StoreCategory?, // 매장 카테고리
    val time: String?, // 픽업 가능 시간 (HH:mm 형식) TODO: 검증로직
    val onlyReservable: Boolean, // 매장 상태
    val lastId: String? = null, // 마지막 검색 paginationToken
) {
    companion object {
        fun from(
            searchText: String? = null, // 검색 키워드
            storeCategory: String? = null,
            time: String? = null,
            onlyReservable: Boolean = false, // 매장 상태 (true: 영업중, false,null: 전체)
            lastId: String? = null, // 마지막 검색 paginationToken
        ): SearchFilter {
            val storeCategoryEnum = storeCategory?.let { StoreEnum.StoreCategory.fromString(it) }
            return SearchFilter(
                searchText = searchText,
                storeCategory = storeCategoryEnum,
                time = time,
                onlyReservable = onlyReservable,
                lastId = lastId, // 마지막 검색 paginationToken
            )
        }
    }
}
