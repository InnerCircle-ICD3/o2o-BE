package com.eatngo.search.domain

import com.eatngo.search.constant.SuggestionType

class SearchSuggestion(
    val keyword: String,
    val type: SuggestionType, // 검색어 타입 (예: "storeName", "foodType" 등)
    val keywordId: Long?, // 검색어와 관련된 ID (예: 매장 ID, 음식 종류 ID 등)
) {
    companion object {
        fun from(
            keyword: String,
            type: SuggestionType,
            keywordId: Long? = null,
        ): SearchSuggestion =
            SearchSuggestion(
                keyword = keyword,
                type = type,
                keywordId = keywordId,
            )
    }
}
