package com.eatngo.redis.repository.search

import com.eatngo.search.infra.SearchMapRedisRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class SearchMapMapRedisRepositoryImpl (
    private val redisTemplate: RedisTemplate<String, String>
) : SearchMapRedisRepository {
    override fun save(key: String, value: String) {
        redisTemplate.opsForValue().set(key, value)
    }

    override fun findByKey(key: String): String? {
        return redisTemplate.opsForValue().get(key)
    }

    override fun deleteByKey(key: String) {
        redisTemplate.delete(key)
    }
}