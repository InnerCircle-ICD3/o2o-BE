package com.eatngo.product.persistence

import com.eatngo.product.domain.Inventory
import com.eatngo.product.entity.InventoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JpaInventoryRepository : JpaRepository<InventoryEntity, Long> {
    fun findTopByProductIdOrderByVersionDesc(productId: Long): Inventory?
    fun deleteByProductId(productId: Long)
}