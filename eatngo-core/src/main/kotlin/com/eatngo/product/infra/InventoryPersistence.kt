package com.eatngo.product.infra

import com.eatngo.product.domain.Inventory

interface InventoryPersistence {
    fun save(inventory: Inventory): Inventory
    fun findTopByProductIdOrderByVersionDesc(productId: Long): Inventory?
    fun deleteById(productId: Long)
}