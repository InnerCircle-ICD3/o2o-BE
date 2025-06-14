package com.eatngo.search.infra

import com.eatngo.search.dto.SearchSuggestionDto

interface SearchSuggestionRedisRepository {
    fun getKey(keyword: String): String

    fun getSuggestsByKey(key: String): List<SearchSuggestionDto>

    fun saveSuggests(
        key: String,
        suggests: List<SearchSuggestionDto>,
    )
}
