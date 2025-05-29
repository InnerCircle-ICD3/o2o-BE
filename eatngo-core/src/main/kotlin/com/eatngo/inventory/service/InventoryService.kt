package com.eatngo.inventory.service

import com.eatngo.common.exception.InventoryException.InventoryNotFound
import com.eatngo.extension.orThrow
import com.eatngo.inventory.domain.Inventory
import com.eatngo.inventory.dto.InventoryDto
import com.eatngo.inventory.infra.InventoryPersistence
import com.eatngo.product.dto.ProductCurrentStockDto
import com.eatngo.product.dto.ProductDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class InventoryService(
    private val inventoryPersistence: InventoryPersistence,
) {

    fun createInventory(productDto: ProductDto): InventoryDto {
        val inventory = Inventory.create(productDto.inventory.quantity, productDto.id!!)
        val savedInventory: Inventory = inventoryPersistence.save(inventory)
        return InventoryDto(savedInventory.quantity)
    }

    fun getInventoryDetails(productId: Long): InventoryDto {
        val inventory: Inventory =
            inventoryPersistence.findTopByProductIdOrderByVersionDesc(productId)
                .orThrow { InventoryNotFound(productId) }
        return InventoryDto(inventory.quantity)
    }

    fun deleteInventory(productId: Long) {
        inventoryPersistence.deleteById(productId)
    }

    fun toggleInventory(productCurrentStockDto: ProductCurrentStockDto): InventoryDto {
        val inventory: Inventory = inventoryPersistence.findTopByProductIdOrderByVersionDesc(productCurrentStockDto.id)
            .orThrow { InventoryNotFound(productCurrentStockDto.id) }
        val changedInventory = inventory.changeStock(productCurrentStockDto.action, productCurrentStockDto.amount)
        val savedInventory: Inventory = inventoryPersistence.save(changedInventory)
        return InventoryDto(savedInventory.quantity)
    }

    fun modifyInventory(productDto: ProductDto): InventoryDto {
        val inventory: Inventory = inventoryPersistence.findTopByProductIdOrderByVersionDesc(productDto.id!!)
            .orThrow { InventoryNotFound(productDto.id!!) }

        val changedInventory: Inventory = inventory.changeInventory(
            quantity = productDto.inventory.quantity,
            stock = productDto.inventory.stock,
        )

        val savedInventory: Inventory = inventoryPersistence.save(changedInventory)
        return InventoryDto(savedInventory.quantity)
    }

}