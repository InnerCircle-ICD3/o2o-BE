package com.eatngo.redis.repository.inventory

import com.eatngo.common.exception.product.StockException.StockEmpty
import com.eatngo.common.exception.product.StockException.StockNotFound
import com.eatngo.inventory.dto.InventoryDto
import com.eatngo.inventory.infra.InventoryCachePersistence
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Repository

@Repository
class InventoryRedisPersistenceImpl(
    private val redisTemplate: RedisTemplate<String, String>,
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

        private const val LUA_ROLLBACK_STOCK = """
            local exists = redis.call('EXISTS', KEYS[1])
            if (exists == 0) then return -1 end
            redis.call('HINCRBY', KEYS[1], 'stock', tonumber(ARGV[1]))
            return 1
        """
    }

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

    override fun findByProductId(productId: Long): InventoryDto? {
        val key = pKey(productId)
        val quantityValue = redisTemplate.opsForHash<String, String>()
            .get(key, "quantity") ?: return null

        val stockValue = redisTemplate.opsForHash<String, String>()
            .get(key, "stock") ?: return null

        return InventoryDto(
            quantity = quantityValue.toInt(),
            stock = stockValue.toInt()
        )
    }

    override fun rollbackStock(productId: Long, stockQuantity: Int) {
        val script = DefaultRedisScript(LUA_ROLLBACK_STOCK, Long::class.java)
        val result = redisTemplate.execute(script, listOf(pKey(productId)), stockQuantity.toString())
            ?: -1L
        if (result == -1L) throw StockNotFound(productId)
    }

    override fun saveHash(productId: Long, inventoryDto: InventoryDto) {
        val key = "inventory:hash:$productId"
        redisTemplate.opsForHash<String, String>().putAll(
            key,
            mapOf(
                "stock" to inventoryDto.stock.toString(),
                "quantity" to inventoryDto.quantity.toString()
            )
        )
    }

    override fun deleteHash(productId: Long) {
        redisTemplate.delete("inventory:hash:$productId")
    }
}
