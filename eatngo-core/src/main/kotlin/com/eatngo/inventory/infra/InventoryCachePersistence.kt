package com.eatngo.inventory.infra

import com.eatngo.inventory.dto.InventoryDto

interface InventoryCachePersistence {
    fun decreaseStock(productId: Long, stockQuantityToDecrease: Int): Int
    fun findByProductId(productId: Long): InventoryDto?
    fun rollbackStock(productId: Long, stockQuantity: Int)
}