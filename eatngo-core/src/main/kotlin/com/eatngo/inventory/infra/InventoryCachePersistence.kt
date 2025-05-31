package com.eatngo.inventory.infra

interface InventoryCachePersistence {
    fun decreaseStock(productId: Long, quantity: Int): Int
}