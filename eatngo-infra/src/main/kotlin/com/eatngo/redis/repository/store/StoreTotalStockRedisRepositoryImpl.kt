package com.eatngo.redis.repository.store

import com.eatngo.store.infra.StoreTotalStockRedisRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Repository
class StoreTotalStockRedisRepositoryImpl(
    private val redisTemplate: RedisTemplate<String, String>
) : StoreTotalStockRedisRepository {

    companion object {
        private const val STORE_TOTAL_STOCK_KEY = "%d:%s:totalCount" // {storeId}:{yyyyMMdd}:totalCount
        private const val TTL_HOURS = 24L
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd")
    }

    override fun setStoreTotalStock(storeId: Long, date: LocalDate, totalStock: Int) {
        val key = generateKey(storeId, date)
        redisTemplate.opsForValue().set(key, totalStock.toString(), Duration.ofHours(TTL_HOURS))
    }

    override fun getStoreTotalStock(storeId: Long, date: LocalDate): Int? {
        val key = generateKey(storeId, date)
        return redisTemplate.opsForValue().get(key)?.toIntOrNull()
    }

    override fun deleteStoreTotalStock(storeId: Long, date: LocalDate) {
        val key = generateKey(storeId, date)
        redisTemplate.delete(key)
    }

    private fun generateKey(storeId: Long, date: LocalDate): String {
        return STORE_TOTAL_STOCK_KEY.format(storeId, date.format(DATE_FORMATTER))
    }
}