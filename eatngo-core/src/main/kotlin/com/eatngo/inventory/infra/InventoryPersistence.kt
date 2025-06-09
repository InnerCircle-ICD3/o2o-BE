package com.eatngo.inventory.infra

import com.eatngo.inventory.domain.Inventory
import java.time.LocalDate

interface InventoryPersistence {
    fun save(inventory: Inventory): Inventory
    fun findTopByProductIdOrderByVersionDesc(productId: Long, localDate: LocalDate): Inventory?
    fun findTopByProductIdOrderByVersionDesc(productId: Long): Inventory?
    fun findLatestByProductIds(productIds: List<Long>): List<Inventory>
    fun deleteByProductId(productId: Long)
    fun updateStock(productId: Long, stockQuantity: Int, localDate: LocalDate): Int
    fun findAllByProductIdIn(productIds: List<Long>, localDate: LocalDate): List<Inventory>
    fun saveAll(inventories: List<Inventory>)
}