package com.eatngo.inventory.batch

import com.eatngo.inventory.domain.Inventory
import com.eatngo.inventory.infra.InventoryPersistence
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class InventorySyncScheduler(
    private val inventoryPersistence: InventoryPersistence,
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val REDIS_KEY_PREFIX = "inventory:"
        private const val CHANGED_SET_KEY = "changedProductIds"
        private const val CHUNK_SIZE = 500

        private const val LUA_POP_AND_DELETE = """
            local members = redis.call('SMEMBERS', KEYS[1])
            redis.call('DEL', KEYS[1])
            return members
        """
    }

    @Scheduled(cron = "0 0/10 * * * ?")
    @Transactional
    fun syncInventoryFromRedisToRdb() {
        val changedIds = fetchAndClearChangedIdsAtomically()

        if (changedIds.isEmpty()) {
            logger.info("동기화할 변경된 재고 ID가 없습니다.")
            return
        }

        val productIds = changedIds.mapNotNull { productId ->
            productId.toLongOrNull().also {
                if (it == null) {
                    logger.warn("잘못된 productId 형식: '$productId' (Long 변환 실패)")
                }
            }
        }

        if (productIds.isEmpty()) {
            logger.info("유효한 productId가 없어 동기화 대상이 없습니다.")
            return
        }

        logger.info("동기화 대상 상품 ID 개수: ${productIds.size}")

        productIds.chunked(CHUNK_SIZE).forEachIndexed { chunkIndex, idChunk ->
            try {
                updateRdbWithSingleChunk(idChunk, chunkIndex)
            } catch (ex: Exception) {
                logger.error("chunk #$chunkIndex 처리 중 오류 발생. IDs=$idChunk", ex)
            }
        }

        logger.info("Redis → RDB 재고 동기화 작업 완료.")
    }

    private fun fetchAndClearChangedIdsAtomically(): List<String> {
        val redisScript = DefaultRedisScript<List<String>>().apply {
            LUA_POP_AND_DELETE.trimIndent()
            resultType = List::class.java as Class<List<String>>
        }

        val result = redisTemplate.execute(
            redisScript,
            listOf(CHANGED_SET_KEY)
        )

        return result ?: emptyList()
    }

    private fun updateRdbWithSingleChunk(
        idChunk: List<Long>,
        chunkIndex: Int
    ) {
        val inventories: MutableList<Inventory> =
            inventoryPersistence.findAllByProductIdIn(idChunk, LocalDate.now()).toMutableList()

        if (inventories.isEmpty()) {
            logger.warn("chunk #$chunkIndex: RDB에서 조회된 엔티티가 없습니다. IDs=$idChunk")
            return
        }

        inventories.forEach { inventory ->
            val redisKey = "$REDIS_KEY_PREFIX${inventory.productId}"
            val stockValue = redisTemplate.opsForHash<String, String>()
                .get(redisKey, "stock")
            val redisStock = stockValue?.toIntOrNull()
            if (redisStock == null) {
                logger.warn("chunk #$chunkIndex: Redis 키 '$redisKey' 의 'stock' 필드가 없거나 파싱 실패. 건너뜁니다.")
                return@forEach
            }
            inventory.changeInventory(stock = redisStock)
        }

        inventoryPersistence.saveAll(inventories)
        logger.info("chunk #$chunkIndex: ${inventories.size}건 재고 업데이트 완료.")
    }
}
