package com.eatngo.search.infra

import com.eatngo.search.constant.SuggestionType
import com.eatngo.search.domain.SearchSuggestion

interface SearchSuggestionRepository {
    fun getSuggestionsByKeyword(
        keyword: String,
        type: SuggestionType? = null,
        size: Int = 5,
    ): List<SearchSuggestion>

    fun saveSuggestionList(suggestList: List<SearchSuggestion>)

    fun saveSuggestion(suggestion: SearchSuggestion)

    fun deleteByKeywordIdList(keywordIdList: List<Long>)
}
