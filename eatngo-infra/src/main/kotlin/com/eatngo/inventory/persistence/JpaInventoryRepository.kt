package com.eatngo.inventory.persistence

import com.eatngo.inventory.domain.Inventory
import com.eatngo.inventory.entity.InventoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JpaInventoryRepository : JpaRepository<InventoryEntity, Long> {
    fun findTopByProductIdOrderByVersionDesc(productId: Long): Inventory?
    fun deleteByProductId(productId: Long)
}