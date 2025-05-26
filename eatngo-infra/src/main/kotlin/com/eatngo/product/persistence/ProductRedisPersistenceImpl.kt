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
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Repository
@Transactional
class ProductRedisPersistenceImpl(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
) : ProductCachePersistence {

    private val hashOps = redisTemplate.opsForHash<String, String>()
    private val stockHashOps = redisTemplate.opsForHash<String, Long>()
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
        hashOps.putAll(
            pKey(product.id!!),
            objectMapper.convertValue<Map<String, String>>(ProductMapper.toEntity(product))
        )
        hashOps.putAll(
            pKey(product.id!!),
            objectMapper.convertValue<Map<String, String>>(product.inventory.stock)
        )
        setOps.add(sKey(product.storeId!!), product.id.toString())
        // TODO ttl fix
        redisTemplate.expire(pKey(product.id!!), Duration.ofSeconds(3000))
    }

    override fun findById(productId: Long): Product? {
        val map = hashOps.entries(pKey(productId))
        if (map.isEmpty()) {
            return null
        }
        return ProductMapper.toDomain(objectMapper.convertValue(map))
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
        // TODO
//        return stockHashOps.entries(productId).toLong()
        return 1
    }

    override fun increaseStock(productId: Long, quantity: Long): Long {
        return stockHashOps.increment(pKey(productId), "stock", quantity)
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