package com.eatngo.inventory.entity

import jakarta.persistence.*
import org.springframework.data.redis.core.RedisHash

@Entity
@Table(name = "inventory")
@RedisHash(value = "inventory", timeToLive = 600)
data class InventoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    val quantity: Int,
    val stock: Int,

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @Version
    var version: Long = 0L,
)