package com.eatngo.product.persistence

import com.eatngo.product.domain.Product
import com.eatngo.product.entity.ProductEntity
import com.eatngo.product.infra.ProductCachePersistence
import com.eatngo.product.mapper.ProductMapper
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class ProductRedisPersistenceImpl(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
) : ProductCachePersistence {

    private val hashOps = redisTemplate.opsForHash<String, String>()
    private val setOps = redisTemplate.opsForSet()

    companion object {
        private fun pKey(id: Long) = "product:$id"
        private fun sKey(storeId: Long) = "store:$storeId:products"

        private const val LUA_DEC_STOCK = """
            local cur = redis.call('HGET', KEYS[1], 'stock')
            if (not cur) then return -2 end
            cur = tonumber(cur); local dec = tonumber(ARGV[1])
            if (cur < dec) then return -1 end
            redis.call('HINCRBY', KEYS[1], 'stock', -dec)
            return cur - dec
        """
    }

    override fun save(product: Product) {
        val productData = objectMapper.convertValue<Map<String, String>>(ProductMapper.toEntity(product))
        hashOps.putAll(pKey(product.id!!), productData + saveStock(product))
        setOps.add(sKey(product.storeId!!), product.id.toString())
        redisTemplate.expire(pKey(product.id!!), Duration.ofHours(1))
    }

    private fun saveStock(product: Product) = ("stock" to product.inventory.stock.toString())

    override fun findById(productId: Long): Product? {
        val productData = hashOps.entries(pKey(productId))
        if (productData.isEmpty()) {
            return null
        }
        return ProductMapper.toDomain(objectMapper.convertValue(productData))
    }

    override fun findAllByStoreId(storeId: Long): List<Product> {
        val ids = setOps.members(sKey(storeId)) ?: return emptyList()
        if (ids.isEmpty()) return emptyList()

        val raw = redisTemplate.executePipelined { conn ->
            ids.forEach { conn.hashCommands().hGetAll(pKey(it.toLong()).toByteArray()) }
        }

        return raw.mapNotNull { bytes ->
            val map = (bytes as Map<ByteArray, ByteArray>)
                .mapKeys { String(it.key) }
                .mapValues { String(it.value) }
            if (map.isEmpty()) null else ProductMapper.toDomain(objectMapper.convertValue<ProductEntity>(map))
        }
    }

    override fun deleteById(productId: Long) {
        val storeId = hashOps.get(pKey(productId), "storeId")?.toLong()
        redisTemplate.delete(pKey(productId))
        storeId?.let { setOps.remove(sKey(it), productId.toString()) }
    }

    override fun findStockById(productId: Long): Long {
        return hashOps.get(pKey(productId), "stock")?.toLong() ?: 0L
    }

    override fun increaseStock(productId: Long, quantity: Long): Long {
        return hashOps.increment(pKey(productId), "stock", quantity) ?: 0L
    }

    override fun decreaseStock(product: Product, quantity: Long): Long {
        val script = DefaultRedisScript(LUA_DEC_STOCK, Long::class.java)
        val afterStock = redisTemplate.execute(script, listOf(pKey(product.id!!)), quantity.toString()) ?: -2L
        when (afterStock) {
            -1L -> throw IllegalStateException("재고 부족")
            -2L -> throw IllegalStateException("재고 정보 없음")
        }
        return afterStock
    }
}