package com.eatngo.inventory.mapper

import com.eatngo.inventory.domain.Inventory
import com.eatngo.inventory.entity.InventoryEntity

class InventoryMapper {

    companion object {
        fun toEntity(inventory: Inventory): InventoryEntity {
            return InventoryEntity(
                id = inventory.id,
                quantity = inventory.quantity,
                stock = inventory.stock,
                version = inventory.version,
                productId = inventory.productId
            )
        }

        fun toDomain(inventoryEntity: InventoryEntity): Inventory {
            return Inventory(
                id = inventoryEntity.id,
                quantity = inventoryEntity.quantity,
                stock = inventoryEntity.stock,
                productId = inventoryEntity.productId,
                version = inventoryEntity.version,
            )
        }
    }
}