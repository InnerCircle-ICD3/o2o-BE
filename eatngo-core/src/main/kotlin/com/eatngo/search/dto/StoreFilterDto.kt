package com.eatngo.search.dto

import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.exception.SearchException
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
            storeCategory: String?,
            time: String?,
            onlyReservable: Boolean = false, // 매장 상태 (true: 영업중, false,null: 전체)
        ): StoreFilterDto {
            val viewCoordinate =
                CoordinateVO.from(latitude, longitude).orThrow {
                    SearchException.SearchInvalidCoordinate(latitude, longitude)
                }

            val filter =
                SearchFilter
                    .from(
                        storeCategory = storeCategory,
                        time = time,
                        onlyReservable = onlyReservable,
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
    val storeCategory: StoreEnum.StoreCategory?, // 매장 카테고리
    val time: String?, // 픽업 가능 시간 (HH:mm 형식) TODO: 검증로직
    val onlyReservable: Boolean, // 매장 상태
) {
    companion object {
        fun from(
            storeCategory: String? = null,
            time: String? = null,
            onlyReservable: Boolean = false, // 매장 상태 (true: 영업중, false,null: 전체)
        ): SearchFilter {
            val storeCategoryEnum = storeCategory?.let { StoreEnum.StoreCategory.fromString(it) }
            return SearchFilter(
                storeCategory = storeCategoryEnum,
                time = time,
                onlyReservable = onlyReservable,
            )
        }
    }
}
