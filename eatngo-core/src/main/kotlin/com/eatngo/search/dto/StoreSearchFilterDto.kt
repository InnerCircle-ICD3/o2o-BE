package com.eatngo.search.dto

import com.eatngo.common.exception.SearchException
import com.eatngo.common.type.CoordinateVO
import com.eatngo.extension.orThrow

data class StoreSearchFilterDto(
    val viewCoordinate: CoordinateVO, // 검색하는 유저의 위치 정보
    val searchText: String, // 검색 키워드
) {
    companion object {
        fun from(
            latitude: Double,
            longitude: Double,
            searchText: String,
        ): StoreSearchFilterDto {
            val viewCoordinate =
                CoordinateVO.from(latitude, longitude).orThrow {
                    SearchException.SearchInvalidCoordinate(latitude, longitude)
                }

            if (searchText.isBlank()) {
                throw SearchException.SearchInvalidFilter()
            }

            return StoreSearchFilterDto(
                viewCoordinate = viewCoordinate,
                searchText = searchText,
            )
        }
    }
}
