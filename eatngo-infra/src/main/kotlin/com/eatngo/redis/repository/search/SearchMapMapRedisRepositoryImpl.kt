package com.eatngo.redis.repository.search

import com.eatngo.common.type.CoordinateVO
import com.eatngo.redis.utils.readValueFromJson
import com.eatngo.redis.utils.writeValueToJson
import com.eatngo.search.dto.SearchStoreMap
import com.eatngo.search.infra.SearchMapRedisRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
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
    @Autowired @Qualifier("stringRedisTemplate")
    private val redisTemplate: RedisTemplate<String, String>,
    @Autowired
    private val objectMapper: ObjectMapper,
) : SearchMapRedisRepository {
    override fun getKey(topLeft: CoordinateVO): String {
        val latitude = topLeft.latitude
        val longitude = topLeft.longitude
        return "searchMap:lat:$latitude:lng:$longitude"
    }

    override fun save(
        key: String,
        values: List<SearchStoreMap>,
    ) {
        val ops = redisTemplate.opsForHash<String, String>()
        values.forEach { storeMap ->
            val json = objectMapper.writeValueToJson(storeMap)
            ops.put(key, storeMap.storeId.toString(), json)
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
