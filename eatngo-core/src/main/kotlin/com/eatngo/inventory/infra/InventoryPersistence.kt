package com.eatngo.inventory.infra

import com.eatngo.inventory.domain.Inventory

interface InventoryPersistence {
    fun save(inventory: Inventory): Inventory
    fun findTopByProductIdOrderByVersionDesc(productId: Long): Inventory?
    fun findLatestByProductIds(productIds: List<Long>): List<Inventory>
    fun deleteByProductId(productId: Long)
    fun updateStock(productId: Long, stockQuantity: Int): Int
    fun findAllByProductIdIn(productIds: List<Long>): List<Inventory>
    fun saveAll(inventories: List<Inventory>)
}