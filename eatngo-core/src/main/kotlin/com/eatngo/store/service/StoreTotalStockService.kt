package com.eatngo.store.service

import com.eatngo.inventory.infra.InventoryPersistence
import com.eatngo.product.infra.ProductPersistence
import com.eatngo.store.infra.StoreTotalStockRedisRepository
import com.eatngo.common.circuitbreaker.annotation.RedisCircuitBreaker
import com.eatngo.common.exception.store.StoreException
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

@Service
class StoreTotalStockService(
    private val storeTotalStockRedisRepository: StoreTotalStockRedisRepository,
    private val inventoryPersistence: InventoryPersistence,
    private val productPersistence: ProductPersistence
) {
    companion object {
        private const val MAX_CACHE_SIZE = 1000
    }

    // 로컬 메모리 캐시 (앱 재시작까지 유지)
    @Volatile
    private var localCache = ConcurrentHashMap<String, CachedStockInfo>()

    private fun cleanupExpiredEntries() {
        if (localCache.size > MAX_CACHE_SIZE * 0.8) {
            val now = System.currentTimeMillis()
            localCache.entries.removeIf { it.value.isExpired(now) }

            if (localCache.size > MAX_CACHE_SIZE) {
                val sortedEntries = localCache.entries.sortedBy { it.value.cachedAt }
                val toRemove = sortedEntries.take(localCache.size - MAX_CACHE_SIZE)
                toRemove.forEach { localCache.remove(it.key) }
            }
        }
    }

    data class CachedStockInfo(
        val stock: Int,
        val cachedAt: Long = System.currentTimeMillis(),
    ) {
        fun isExpired(now: Long = System.currentTimeMillis()): Boolean =
            now - cachedAt > 300_000L // 5분
    }

    /**
     * 매장의 총 재고 수량을 조회 (프론트 응답용)
     * @return 재고 수량 (-1: 오늘 판매 안함, 0 이상: 실제 재고)
     */
    @RedisCircuitBreaker(
        name = "store-total-stock-get",
        fallbackMethod = "getStoreTotalStockFallback",
        failureThreshold = 3,
        timeoutMinutes = 1L
    )
    fun getStoreTotalStockForResponse(storeId: Long, date: LocalDate = LocalDate.now()): Int {
        val redisStock = storeTotalStockRedisRepository.getStoreTotalStock(storeId, date)
        return redisStock ?: -1 // null이면 -1 반환 (오늘 판매 안함)
    }

    /**
     * fallback 메서드
     */
    fun getStoreTotalStockFallback(storeId: Long, date: LocalDate, ex: Exception): Int {
        val cacheKey = "${storeId}:${date}"
        
        // 로컬 메모리 캐시 확인
        localCache[cacheKey]?.let { cached ->
            if (!cached.isExpired()) {
                return cached.stock
            } else {
                localCache.remove(cacheKey)
            }
        }

        // 위에서 로컬 캐시로 해결 안 되면 DB 조회
        return try {
            if (shouldAllowDbQuery(storeId)) {
                val dbStock = calculateTotalStockFromDBWithLimit(storeId)
                localCache[cacheKey] = CachedStockInfo(dbStock)
                
                dbStock
            } else {
                // DB 조회도 제한된 경우 - 기본값 반환
                getDefaultStock(storeId)
            }
        } catch (dbEx: Exception) {
            StoreException.StoreTotalStockException(storeId, dbEx)
            getDefaultStock(storeId)
        }
    }
    
    // DB 조회 제한 로직 (매장별로 분당 최대 2회)
    private val dbQueryTracker = ConcurrentHashMap<Long, MutableList<Long>>()
    
    private fun shouldAllowDbQuery(storeId: Long): Boolean {
        val now = System.currentTimeMillis()
        val queries = dbQueryTracker.computeIfAbsent(storeId) { mutableListOf() }
        
        queries.removeIf { it < now - 60_000L }
        
        if (queries.size >= 2) return false
        
        queries.add(now)
        return true
    }
    
    /**
     * 제한된 DB 조회 - 필수 정보만 조회
     */
    private fun calculateTotalStockFromDBWithLimit(storeId: Long): Int {
        val activeProductCount = productPersistence.countActiveProductsByStoreId(storeId)
        
        if (activeProductCount == 0L) return 0

        val products = productPersistence.findAllActivatedProductByStoreId(storeId)
        val productIds = products.map { it.id }
        val latestInventories = inventoryPersistence.findLatestByProductIds(productIds)
        return latestInventories.sumOf { it.stock }
    }
    
    /**
     * 기본 재고값 반환 (Redis, DB 모두 실패 시)
     */
    private fun getDefaultStock(storeId: Long): Int {
        val cacheKey = "${storeId}:${LocalDate.now()}"
        localCache[cacheKey]?.let { cached ->
            return cached.stock
        }
        
        return -1 // 판매 안함으로 처리
    }

    /**
     * 매장의 총 재고 수량을 Redis에 설정 (이벤트 처리용)
     */
    @RedisCircuitBreaker(
        name = "store-total-stock-set",
        fallbackMethod = "setStoreTotalStockFallback",
        failureThreshold = 5,
        timeoutMinutes = 2L
    )
    fun setStoreTotalStock(storeId: Long, newTotalStock: Int, date: LocalDate = LocalDate.now()) {
        // 재고가 0 이상인 경우에만 Redis에 저장(-1은 애초에 재고 설정 안 한거라 저장할 필요가 없음)
        if (newTotalStock >= 0) {
            storeTotalStockRedisRepository.setStoreTotalStock(storeId, date, newTotalStock)
            
            val cacheKey = "${storeId}:${date}"
            localCache[cacheKey] = CachedStockInfo(newTotalStock)
        } else {
            storeTotalStockRedisRepository.deleteStoreTotalStock(storeId, date)
            localCache.remove("${storeId}:${date}")
        }
    }

    /**
     * setStoreTotalStock의 fallback 메서드 - 로컬 캐시만 업데이트
     */
    fun setStoreTotalStockFallback(storeId: Long, newTotalStock: Int, date: LocalDate, ex: Exception) {
        val cacheKey = "${storeId}:${date}"
        if (newTotalStock >= 0) {
            localCache[cacheKey] = CachedStockInfo(newTotalStock)
        } else {
            localCache.remove(cacheKey)
        }
    }
}
