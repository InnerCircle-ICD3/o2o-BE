package com.eatngo.mongo.search.entity

import com.eatngo.search.constant.SuggestionType
import com.eatngo.search.domain.SearchSuggestion
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "SearchSuggestion")
class SearchSuggestionEntity(
    @Id
    var id: String, // keyword_type_id 조합으로 유니크하게 생성
    var keyword: String,
    var type: Int, // 검색어 타입 (1: "storeName", 2: "foodType" 등)
    var keywordId: Long? = null, // 검색어와 관련된 ID (예: 매장 ID, 음식 종류 ID 등)
) {
    fun to(): SearchSuggestion =
        SearchSuggestion(
            keyword = keyword,
            type =
                SuggestionType.fromCode(type)
                    ?: throw IllegalArgumentException("Invalid suggestion type code: $type"),
            keywordId = keywordId,
        )

    companion object {
        fun from(searchSuggestion: SearchSuggestion): SearchSuggestionEntity =
            SearchSuggestionEntity(
                id =
                    this.getId(
                        searchSuggestion.keyword,
                        searchSuggestion.type.code,
                        searchSuggestion.keywordId,
                    ),
                keyword = searchSuggestion.keyword,
                type = searchSuggestion.type.code,
                keywordId = searchSuggestion.keywordId,
            )

        fun getId(
            keyword: String,
            type: Int,
            keywordId: Long? = null,
        ): String = normalizeKeywordForId(keyword, type) + (keywordId?.let { "_$it" } ?: "")

        fun normalizeKeywordForId(
            keyword: String,
            type: Int,
        ): String {
            return "$keyword $type"
                .lowercase()
                .trim()
                .replace("\\s+".toRegex(), "_") // 연속 공백 → "_"
                .replace("[^a-z0-9_가-힣]".toRegex(), "") // 특수문자 제거 (선택적)
        }
    }
}
