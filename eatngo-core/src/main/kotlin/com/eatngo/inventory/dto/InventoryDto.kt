package com.eatngo.inventory.dto

import com.eatngo.inventory.domain.Inventory

data class InventoryDto(
    val quantity: Int,
    val stock: Int = quantity,
) {
    companion object {
        fun from(inventory: Inventory): InventoryDto {
            return InventoryDto(
                quantity = inventory.quantity,
                stock = inventory.stock,
            )
        }
    }
}