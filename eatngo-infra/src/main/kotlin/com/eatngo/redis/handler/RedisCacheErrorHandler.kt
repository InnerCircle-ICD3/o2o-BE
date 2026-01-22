package com.eatngo.redis.handler

import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.cache.interceptor.CacheErrorHandler
import org.springframework.stereotype.Component

@Component
class RedisCacheErrorHandler : CacheErrorHandler {
    companion object {
        private val log = LoggerFactory.getLogger(RedisCacheErrorHandler::class.java)
    }

    override fun handleCacheGetError(exception: RuntimeException, cache: Cache, key: Any) {
        log.warn("Cache get failed for cache: {}, key: {}. Falling back to DB.", cache.name, key)
        // 예외를 무시하고 DB 조회 허용
    }

    override fun handleCachePutError(exception: RuntimeException, cache: Cache, key: Any, value: Any?) {
        log.warn("Cache put failed for cache: {}, key: {}. DB already updated.", cache.name, key)
        // 캐시 업데이트 실패는 무시 (DB 이미 반영됨)
    }

    override fun handleCacheEvictError(exception: RuntimeException, cache: Cache, key: Any) {
        log.warn("Cache evict failed for cache: {}, key: {}.", cache.name, key)
    }

    override fun handleCacheClearError(exception: RuntimeException, cache: Cache) {
        log.warn("Cache clear failed for cache: {}", cache.name)
    }
}
