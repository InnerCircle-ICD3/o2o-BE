package com.eatngo.redis.repository.inventory

import com.eatngo.common.exception.StockException.StockEmpty
import com.eatngo.common.exception.StockException.StockNotFound
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
    }

    override fun decreaseStock(productId: Long, quantity: Int): Int {
        val script = DefaultRedisScript(LUA_DEC_STOCK, Long::class.java)
        val result = redisTemplate.execute(script, listOf(pKey(productId)), quantity.toString())
            ?: -2L
        when (result) {
            -1L -> throw StockEmpty(productId)
            -2L -> throw StockNotFound(productId)
        }
        return result.toInt()
    }
}