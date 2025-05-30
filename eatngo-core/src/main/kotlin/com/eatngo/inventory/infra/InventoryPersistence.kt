package com.eatngo.inventory.infra

import com.eatngo.inventory.domain.Inventory

interface InventoryPersistence {
    fun save(inventory: Inventory): Inventory
    fun findTopByProductIdOrderByVersionDesc(productId: Long): Inventory?
    fun deleteByProductId(productId: Long)
}