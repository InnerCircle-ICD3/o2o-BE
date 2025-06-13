package com.eatngo.search.dto

import com.eatngo.search.constant.SuggestionType

data class SearchSuggestionResultDto(
    val suggestionList: List<SearchSuggestionDto>, // 추천 검색어 목록
) {
    companion object {
        fun from(
            storeNameList: List<SearchSuggestionDto>,
            foodList: List<SearchSuggestionDto>,
        ): SearchSuggestionResultDto {
            val combined =
                buildList {
                    addAll(storeNameList)
                    addAll(foodList)
                }

            return SearchSuggestionResultDto(
                suggestionList = combined,
            )
        }
    }
}

data class SearchSuggestionDto(
    val value: String, // 추천 검색어
    val field: SuggestionType, // 추천 검색어 출처 필드(예: 매장 or 음식)
    val storeId: Long? = null, // 추천 검색어가 매장명인 경우 매장 ID
) {
    companion object {
        fun from(
            value: String,
            field: SuggestionType,
            storeId: Long? = null,
        ): SearchSuggestionDto =
            SearchSuggestionDto(
                value = value,
                field = field,
                storeId = storeId,
            )
    }
}
