package com.eatngo.redis.repository.inventory

import com.eatngo.common.exception.product.StockException.StockEmpty
import com.eatngo.common.exception.product.StockException.StockNotFound
import com.eatngo.inventory.dto.InventoryDto
import com.eatngo.inventory.infra.InventoryCachePersistence
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Repository

@Repository
class InventoryRedisPersistenceImpl(
//    @Qualifier("typeRedisTemplate")
    private val redisTemplate: RedisTemplate<String, String>,
) : InventoryCachePersistence {

    companion object {
        private fun luaKey(id: Long) = "inventory:hash:$id"
        private fun pKey(productId: Long) = "inventory::$productId"

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
        val keys = redisTemplate.keys("inventory:hash:*")
        println("🔥 [Redis Keys] Found ${keys.size} keys:")
        keys.forEach { key ->
            val entries = redisTemplate.opsForHash<String, String>().entries(key)
            println("  - Key: $key")
            println("    -> Entries: $entries")
        }
        val script = DefaultRedisScript(LUA_DEC_STOCK, Long::class.java)
        val result = redisTemplate.execute(
            script,
            listOf(luaKey(productId)),
            stockQuantityToDecrease.toString()
        ) ?: -2L
        when (result) {
            -1L -> throw StockEmpty(productId)
            -2L -> throw StockNotFound(productId)
            // TODO 재시도 처리 로직 필요 -> fail 시 DB에도 한번 다녀오도록 해야 함 ~> redis에는 재고 정보가 없어질 수도 있기 때문
        }

        updateInventoryCacheWithClassInfo(productId)
        return result.toInt()
    }

    private fun updateInventoryCacheWithClassInfo(productId: Long) {
        val updated = findByProductId(productId) ?: return

        val mapperWithTyping = ObjectMapper().activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL
        )

        val json = mapperWithTyping.writeValueAsString(updated)

        redisTemplate.opsForValue().set(pKey(productId), json)

        // 상품 캐시 삭제 필요 ~> ProductDto의 InventoryDto 가 갱신되지 않기 때문
        redisTemplate.delete("product::$productId")
    }

    override fun findByProductId(productId: Long): InventoryDto? {
        val key = luaKey(productId)
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
        val result = redisTemplate.execute(script, listOf(luaKey(productId)), stockQuantity.toString())
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
