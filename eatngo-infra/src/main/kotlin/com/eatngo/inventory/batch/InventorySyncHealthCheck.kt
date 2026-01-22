package com.eatngo.inventory.batch

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class InventorySyncHealthCheck(
    private val stringRedisTemplate: RedisTemplate<String, String>
) {
    companion object {
        private val log = LoggerFactory.getLogger(javaClass)
    }

    /**
     * Redis가 사용 가능한지 확인합니다.
     *
     * @return Redis가 정상이면 true, 그렇지 않으면 false
     */
    fun isRedisAvailable(): Boolean {
        return try {
            stringRedisTemplate.connectionFactory?.connection?.use { connection ->
                connection.ping()
            }
            true
        } catch (ex: Exception) {
            log.warn("Redis health check failed: ${ex.message}")
            false
        }
    }
}
