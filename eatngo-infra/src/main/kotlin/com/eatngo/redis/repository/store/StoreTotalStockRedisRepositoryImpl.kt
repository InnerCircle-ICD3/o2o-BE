package com.eatngo.redis.repository.store

import com.eatngo.store.infra.StoreTotalStockRedisRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Repository
class StoreTotalStockRedisRepositoryImpl(
    private val stringRedisTemplate: StringRedisTemplate,
) : StoreTotalStockRedisRepository {
    companion object {
        private const val STORE_TOTAL_STOCK_KEY = "%d:%s:totalCount" // {storeId}:{yyyyMMdd}:totalCount
        private const val TTL_HOURS = 24L
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd")
    }

    override fun setStoreTotalStock(
        storeId: Long,
        date: LocalDate,
        totalStock: Int,
    ) {
        require(totalStock >= 0) { "총 재고는 음수가 될 수 없습니다. Redis에 저장 실패: (storeId: $storeId, totalStock: $totalStock)" }
        val key = generateKey(storeId, date)
        stringRedisTemplate.opsForValue().set(key, totalStock.toString(), Duration.ofHours(TTL_HOURS))
    }

    override fun getStoreTotalStock(
        storeId: Long,
        date: LocalDate,
    ): Int? {
        val key = generateKey(storeId, date)
        return stringRedisTemplate.opsForValue().get(key)?.toIntOrNull()
    }

    override fun getStoreTotalStockMap(
        storeIdList: List<Long>,
        date: LocalDate,
    ): Map<Long, Int> {
        val keys = storeIdList.map { generateKey(it, date) }
        val values =
            stringRedisTemplate.opsForValue().multiGet(keys)
                ?: List(storeIdList.size) { null } // 모든 key miss 시에도 사이즈 보존
        return storeIdList.zip(values).associate { (storeId, value) ->
            storeId to (value?.toIntOrNull() ?: -1) // null은 -1로 처리
        }
    }

    override fun deleteStoreTotalStock(
        storeId: Long,
        date: LocalDate,
    ) {
        val key = generateKey(storeId, date)
        stringRedisTemplate.delete(key)
    }

    private fun generateKey(
        storeId: Long,
        date: LocalDate,
    ): String = STORE_TOTAL_STOCK_KEY.format(storeId, date.format(DATE_FORMATTER))
}
