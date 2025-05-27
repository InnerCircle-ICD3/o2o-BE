package com.eatngo.redis.repository.search

import com.eatngo.common.type.Coordinate
import com.eatngo.redis.utils.readValueFromJson
import com.eatngo.redis.utils.writeValueToJson
import com.eatngo.search.dto.SearchStoreMap
import com.eatngo.search.infra.SearchMapRedisRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

/**
 * 위치기반 검색 최적화를 위한 위경도 박스 단위 캐싱
 * TODO: TTL 설정 필요
 * Key : Box.topLeft
 * Value : List<SearchStoreMap>
 */
@Repository
class SearchMapMapRedisRepositoryImpl(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : SearchMapRedisRepository {
    override fun getKey(topLeft: Coordinate): String {
        val lat = topLeft.lat
        val lng = topLeft.lng
        return "searchMap:lat:$lat:lng:$lng"
    }

    override fun save(
        key: String,
        values: List<SearchStoreMap>,
    ) {
        val ops = redisTemplate.opsForHash<String, String>()
        values.forEach { storeMap ->
            val json = objectMapper.writeValueToJson(storeMap)
            ops.put(key, storeMap.storeID.toString(), json)
        }
    }

    override fun findByKey(key: String): List<SearchStoreMap> {
        val ops = redisTemplate.opsForHash<String, String>()
        val allValues = ops.entries(key)
        return allValues.values.map {
            objectMapper.readValueFromJson(it)
        }
    }

    override fun deleteByKey(key: String) {
        redisTemplate.delete(key)
    }
}
