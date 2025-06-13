package com.eatngo.search.infra

import com.eatngo.search.domain.SearchSuggestion

interface SearchSuggestionRepository {
    fun getSuggestionsByKeyword(
        keyword: String,
        type: String?,
        size: Int = 5,
    ): List<SearchSuggestion>

    fun saveSuggestionList(suggestList: List<SearchSuggestion>)

    fun saveSuggestion(suggestion: SearchSuggestion)
}
