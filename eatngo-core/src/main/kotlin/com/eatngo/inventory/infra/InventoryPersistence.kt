package com.eatngo.inventory.infra

import com.eatngo.inventory.domain.Inventory

interface InventoryPersistence {
    fun save(inventory: Inventory): Inventory
    fun findTopByProductIdOrderByVersionDesc(productId: Long): Inventory?
    fun deleteByProductId(productId: Long)

    // 현재 점주만 사용 중 -> 점주가 재고를 토글 형식으로 증가/차감 시킬 때 사용
    fun updateStock(productId: Long, stockQuantity: Int): Int
}