package com.eatngo.inventory.persistence

import com.eatngo.inventory.domain.Inventory
import com.eatngo.inventory.infra.InventoryCachePersistence
import com.eatngo.inventory.infra.InventoryPersistence
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class InventorySyncScheduler(
    private val inventoryPersistence: InventoryPersistence,
    private val redisTemplate: RedisTemplate<String, String>
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val REDIS_KEY_PREFIX = "inventory:"
        private const val SCAN_COUNT = 1000L
        private const val CHUNK_SIZE = 500
    }

    // 스케줄링 시간 조정 필요!
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    fun syncInventoryFromRedisToRdb() {
        val productStockMap = fetchRedisProductStocks()
        if (productStockMap.isEmpty()) {
            logger.info("동기화할 Redis 재고 데이터가 없습니다.")
            return
        }

        logger.info("동기화 대상 상품 개수: ${productStockMap.size}")
        updateRdbInChunks(productStockMap)
        logger.info("Redis → RDB 재고 동기화 작업 완료.")
    }

    private fun fetchRedisProductStocks(): MutableMap<Long, Int> {
        val scanOptions = ScanOptions.scanOptions()
            .match("$REDIS_KEY_PREFIX*")
            .count(SCAN_COUNT)
            .build()

        val cursor = redisTemplate.scan(scanOptions)
        val productStockMap = mutableMapOf<Long, Int>()

        cursor.use {
            it.forEachRemaining { key ->
                val productId = key.removePrefix(REDIS_KEY_PREFIX).toLongOrNull()
                if (productId == null) {
                    logger.warn("Redis 키 '$key' 에서 productId 파싱 실패. 건너뜁니다.")
                    return@forEachRemaining
                }

                val stockValue = redisTemplate.opsForHash<String, String>()
                    .get(key, "stock")
                val stock = stockValue?.toIntOrNull()
                if (stock == null) {
                    logger.warn("Redis 키 '$key' 의 'stock' 필드가 없거나 파싱 실패. 건너뜁니다.")
                    return@forEachRemaining
                }

                productStockMap[productId] = stock
            }
        }
        return productStockMap
    }

    private fun updateRdbInChunks(productStockMap: MutableMap<Long, Int>) {
        val productIds = productStockMap.keys.toList()
        productIds.chunked(CHUNK_SIZE).forEachIndexed { chunkIndex, idChunk ->
            try {
                updateRdbWithSingleChunk(idChunk, chunkIndex, productStockMap)
            } catch (ex: Exception) {
                logger.error("chunk #$chunkIndex 처리 중 오류 발생. IDs=$idChunk", ex)
            }
        }
    }

    private fun updateRdbWithSingleChunk(
        idChunk: List<Long>,
        chunkIndex: Int,
        productStockMap: MutableMap<Long, Int>
    ) {
        val inventories: MutableList<Inventory> =
            inventoryPersistence.findAllByProductIdIn(idChunk).toMutableList()

        if (inventories.isEmpty()) {
            logger.warn("chunk #$chunkIndex: RDB에서 조회된 엔티티가 없습니다. IDs=$idChunk")
        } else {
            inventories.forEach { inventory ->
                val redisStock = productStockMap[inventory.productId]
                if (redisStock != null) {
                    inventory.changeInventory(stock = redisStock)
                }
            }
            inventoryPersistence.saveAll(inventories)
            logger.info("chunk #$chunkIndex: ${inventories.size}건 재고 업데이트 완료.")
        }
    }

}