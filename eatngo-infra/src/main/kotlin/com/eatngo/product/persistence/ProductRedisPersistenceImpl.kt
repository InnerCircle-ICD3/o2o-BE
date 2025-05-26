package com.eatngo.product.persistence

import com.eatngo.product.domain.Product
import com.eatngo.product.entity.ProductEntity
import com.eatngo.product.infra.ProductCachePersistence
import com.eatngo.product.mapper.ProductMapper
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import org.springframework.data.redis.core.RedisTemplate
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
    }

    override fun save(product: Product) {
        hashOps.putAll(
            pKey(product.id!!),
            objectMapper.convertValue<Map<String, String>>(ProductMapper.toEntity(product))
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
}