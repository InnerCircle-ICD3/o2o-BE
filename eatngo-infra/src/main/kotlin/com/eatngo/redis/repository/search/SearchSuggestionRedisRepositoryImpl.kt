package com.eatngo.redis.repository.search

import com.eatngo.redis.utils.readValueFromJson
import com.eatngo.redis.utils.writeValueToJson
import com.eatngo.search.dto.SearchSuggestionDto
import com.eatngo.search.infra.SearchSuggestionRedisRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

/**
 * 추천 검색어 캐싱
 */
@Repository
class SearchSuggestionRedisRepositoryImpl(
    @Autowired @Qualifier("stringRedisTemplate")
    private val redisTemplate: RedisTemplate<String, String>,
    @Autowired
    private val objectMapper: ObjectMapper,
) : SearchSuggestionRedisRepository {
    override fun getKey(keyword: String): String = "searchSuggestion:$keyword"

    override fun getSuggestsByKey(key: String): List<SearchSuggestionDto> {
        val ops = redisTemplate.opsForHash<String, String>()
        val allValues = ops.entries(key)
        return allValues.values.map {
            objectMapper.readValueFromJson(it)
        }
    }

    override fun saveSuggests(
        key: String,
        suggests: List<SearchSuggestionDto>,
    ) {
        val ops = redisTemplate.opsForHash<String, String>()
        suggests.forEach { suggestion ->
            val json = objectMapper.writeValueToJson(suggestion)
            ops.put(key, suggestion.value, json)
        }
    }
}
