package com.eatngo.redis.repository.inventory

import com.eatngo.common.exception.InventoryException.InventoryNotFound
import com.eatngo.common.exception.StockException.StockEmpty
import com.eatngo.common.exception.StockException.StockNotFound
import com.eatngo.extension.orThrow
import com.eatngo.inventory.domain.Inventory
import com.eatngo.inventory.dto.InventoryDto
import com.eatngo.inventory.infra.InventoryCachePersistence
import com.eatngo.inventory.infra.InventoryPersistence
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Repository

@Repository
class InventoryRedisPersistenceImpl(
    private val redisTemplate: RedisTemplate<String, String>,
    private val inventoryPersistence: InventoryPersistence
) : InventoryCachePersistence {

    companion object {
        private fun pKey(id: Long) = "inventory:$id"

        private const val LUA_DEC_STOCK = """
            local cur = redis.call('HGET', KEYS[1], 'stock')
            if (not cur) then return -2 end
            cur = tonumber(cur); local dec = tonumber(ARGV[1])
            if (cur < dec) then return -1 end
            redis.call('HINCRBY', KEYS[1], 'stock', -dec)
            return cur - dec
        """

        private const val LUA_INC_STOCK = """
            local cur = redis.call('HGET', KEYS[1], 'stock')
            if (not cur) then return -2 end
            local inc = tonumber(ARGV[1])
            redis.call('HINCRBY', KEYS[1], 'stock', inc)
            return tonumber(cur) + inc
        """
    }

    @CacheEvict("inventory", key = "#productId")
    override fun decreaseStock(productId: Long, stockQuantityToDecrease: Int): Int {
        val script = DefaultRedisScript(LUA_DEC_STOCK, Long::class.java)
        val result = redisTemplate.execute(script, listOf(pKey(productId)), stockQuantityToDecrease.toString())
            ?: -2L
        when (result) {
            -1L -> throw StockEmpty(productId)
            -2L -> throw StockNotFound(productId)
        }
        return result.toInt()
    }

    @Cacheable("inventory", key = "#productId")
    override fun findByProductId(productId: Long): InventoryDto? {
        val inventory: Inventory = inventoryPersistence.findTopByProductIdOrderByVersionDesc(productId)
            .orThrow { InventoryNotFound(productId) }

        return InventoryDto(
            quantity = inventory.quantity,
            stock = inventory.stock
        )
    }

    @CacheEvict("inventory", key = "#productId")
    override fun rollbackStock(productId: Long, stockQuantityToIncrease: Int): Int {
        val script = DefaultRedisScript(LUA_INC_STOCK, Long::class.java)
        val result = redisTemplate.execute(script, listOf(pKey(productId)), stockQuantityToIncrease.toString())
            ?: -2L
        when (result) {
            -2L -> throw StockNotFound(productId)
        }
        return result.toInt()
    }
}