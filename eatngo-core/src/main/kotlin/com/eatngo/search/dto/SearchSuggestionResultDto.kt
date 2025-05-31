package com.eatngo.search.dto

data class SearchSuggestionResultDto(
    val suggestionList: List<SearchSuggestionDto>, // 추천 검색어 목록
) {
    companion object {
        fun from(
            storeNameList: List<SearchSuggestionDto>,
            foodList: List<SearchSuggestionDto>,
        ): SearchSuggestionResultDto {
            // TODO: Mock 삭제 후 var -> val로 변경 필요
            var combined =
                buildList {
                    addAll(storeNameList)
                    addAll(foodList)
                }

            // TODO: 테스트 기간 Mock 데이터 생성
            if (combined.isEmpty()) {
                // Mock 데이터 생성
                combined = SearchSuggestionDto.getMockSearchSuggestionList()
            }

            return SearchSuggestionResultDto(
                suggestionList = combined,
            )
        }
    }
}

data class SearchSuggestionDto(
    val value: String, // 추천 검색어
    val field: String, // 추천 검색어 출처 필드(예: 매장 or 음식) TODO: enum으로 변경 필요
    val storeId: Long? = null, // 추천 검색어가 매장명인 경우 매장 ID
) {
    companion object {
        fun from(
            value: String,
            field: String,
            storeId: Long? = null,
        ): SearchSuggestionDto =
            SearchSuggestionDto(
                value = value,
                field = field,
                storeId = storeId,
            )

        // TODO: 삭제 예정 -> 테스트 기간 Mock 데이터 생성
        fun getMockSearchSuggestionList(): List<SearchSuggestionDto> =
            listOf(
                SearchSuggestionDto(value = "피자가게", field = "storeName", storeId = 1L),
                SearchSuggestionDto(value = "피자나라 치킨공주", field = "storeName", storeId = 2L),
                SearchSuggestionDto(value = "피자", field = "foodCategory"),
                SearchSuggestionDto(value = "피자치킨", field = "foodCategory"),
                SearchSuggestionDto(value = "김치피자탕수육", field = "foodCategory"),
            )
    }
}

data class AutoCompleteStoreNameDto(
    val storeId: Long,
    val storeName: String,
) {
    companion object {
        fun from(
            storeId: Long,
            storeName: String,
        ): AutoCompleteStoreNameDto =
            AutoCompleteStoreNameDto(
                storeId = storeId,
                storeName = storeName,
            )
    }
}
