package com.eatngo.redis.repository.inventory

import com.eatngo.common.circuitbreaker.exception.CircuitBreakerException
import com.eatngo.common.exception.product.StockException.StockEmpty
import com.eatngo.common.exception.product.StockException.StockNotFound
import com.eatngo.inventory.dto.InventoryDto
import com.eatngo.inventory.infra.InventoryCachePersistence
import com.eatngo.inventory.infra.PartitionedRedisSetRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Repository

@Repository
class InventoryRedisPersistenceImpl(
    @Qualifier("stringRedisTemplate")
    private val stringRedisTemplate: RedisTemplate<String, String>,
    @Qualifier("typeRedisTemplate")
    private val typeRedisTemplate: RedisTemplate<String, Any>,
    private val partitionedRedisSetRepository: PartitionedRedisSetRepository,
    @Value("\${inventory.partition.count:10}")
    private val partitionCount: Int,
) : InventoryCachePersistence {

    companion object {
        private fun luaKey(id: Long) = "inventory:hash:$id"
        private fun pKey(productId: Long) = "inventory::$productId"
        private const val CHANGED_SET_KEY = "changedProductIds"
        private const val PENDING_SET_KEY = "pendingSyncProductIds"

        private const val LUA_DEC_STOCK = """
            local cur = redis.call('HGET', KEYS[1], 'stock')
            if (not cur) then return -2 end
            cur = tonumber(cur); local dec = tonumber(ARGV[1])
            if (cur < dec) then return -1 end
            redis.call('HINCRBY', KEYS[1], 'stock', -dec)
            return cur - dec
        """

        private const val LUA_ROLLBACK_STOCK = """
            local exists = redis.call('EXISTS', KEYS[1])
            if (exists == 0) then return -1 end
            redis.call('HINCRBY', KEYS[1], 'stock', tonumber(ARGV[1]))
            return 1
        """
    }

    override fun decreaseStock(productId: Long, stockQuantityToDecrease: Int): Int {
        return try {
            val script = DefaultRedisScript(LUA_DEC_STOCK, Long::class.java)
            val result = stringRedisTemplate.execute(
                script,
                listOf(luaKey(productId)),
                stockQuantityToDecrease.toString()
            ) ?: throw CircuitBreakerException("Redis execution returned null for productId: $productId")

            when (result) {
                -1L -> throw StockEmpty(productId)  // BusinessException - Circuit Breaker 미포함
                -2L -> throw StockNotFound(productId)  // BusinessException - Circuit Breaker 미포함
                else -> {
                    updateInventoryCacheWithClassInfo(productId)
                    result.toInt()
                }
            }
        } catch (e: StockEmpty) {
            throw e  // 비즈니스 예외는 그대로 전파
        } catch (e: StockNotFound) {
            throw e  // 비즈니스 예외는 그대로 전파
        } catch (e: CircuitBreakerException) {
            throw e  // CircuitBreakerException은 그대로 전파
        } catch (e: Exception) {
            // Redis 연결 실패 등 시스템 장애만 CircuitBreakerException으로 래핑
            throw CircuitBreakerException("Redis operation failed for productId: $productId", e)
        }
    }

    private fun updateInventoryCacheWithClassInfo(productId: Long) {
        val updated = findByProductId(productId) ?: return
        typeRedisTemplate.opsForValue().set(pKey(productId), updated)

        // 상품 캐시 삭제 필요 ~> ProductDto의 InventoryDto 가 갱신되지 않기 때문
        stringRedisTemplate.delete("product::$productId")
    }

    override fun findByProductId(productId: Long): InventoryDto? {
        val key = luaKey(productId)
        val quantityValue = stringRedisTemplate.opsForHash<String, String>()
            .get(key, "quantity") ?: return null

        val stockValue = stringRedisTemplate.opsForHash<String, String>()
            .get(key, "stock") ?: return null

        return InventoryDto(
            quantity = quantityValue.toInt(),
            stock = stockValue.toInt()
        )
    }

    override fun rollbackStock(productId: Long, stockQuantity: Int): Int {
        val script = DefaultRedisScript(LUA_ROLLBACK_STOCK, Long::class.java)
        val result = stringRedisTemplate.execute(script, listOf(luaKey(productId)), stockQuantity.toString())
            ?: -1L
        if (result == -1L) throw StockNotFound(productId)

        updateInventoryCacheWithClassInfo(productId)
        return result.toInt()
    }

    override fun saveHash(productId: Long, inventoryDto: InventoryDto) {
        val key = "inventory:hash:$productId"
        stringRedisTemplate.opsForHash<String, String>().putAll(
            key,
            mapOf(
                "stock" to inventoryDto.stock.toString(),
                "quantity" to inventoryDto.quantity.toString()
            )
        )
    }

    override fun deleteHash(productId: Long) {
        stringRedisTemplate.delete("inventory:hash:$productId")
    }

    override fun addChangedProductId(productId: Long) {
        partitionedRedisSetRepository.add(CHANGED_SET_KEY, productId, partitionCount)
    }

    override fun addPendingSyncProductId(productId: Long) {
        partitionedRedisSetRepository.add(PENDING_SET_KEY, productId, partitionCount)
    }
}
