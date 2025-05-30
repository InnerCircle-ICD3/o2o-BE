package com.eatngo.inventory.service

import com.eatngo.common.exception.InventoryException.InventoryNotFound
import com.eatngo.extension.orThrow
import com.eatngo.inventory.domain.Inventory
import com.eatngo.inventory.dto.InventoryDto
import com.eatngo.inventory.infra.InventoryPersistence
import com.eatngo.product.dto.ProductCurrentStockDto
import com.eatngo.product.dto.ProductDto
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class InventoryService(
    private val inventoryPersistence: InventoryPersistence,
) {

    @CachePut("inventory", key = "#productDto.id")
    fun createInventory(productDto: ProductDto): InventoryDto {
        val inventory = Inventory.create(productDto.inventory.quantity, productDto.id!!)
        val savedInventory: Inventory = inventoryPersistence.save(inventory)
        return InventoryDto(savedInventory.quantity)
    }

    @Cacheable("inventory", key = "#productId")
    fun getInventoryDetails(productId: Long): InventoryDto {
        val inventory: Inventory =
            inventoryPersistence.findTopByProductIdOrderByVersionDesc(productId)
                .orThrow { InventoryNotFound(productId) }
        return InventoryDto(inventory.quantity)
    }

    @CacheEvict("inventory", key = "#productId")
    fun deleteInventory(productId: Long) {
        inventoryPersistence.deleteByProductId(productId)
    }

    @CacheEvict("inventory", key = "#productCurrentStockDto.id")
    fun toggleInventory(productCurrentStockDto: ProductCurrentStockDto): InventoryDto {
        val inventory: Inventory = inventoryPersistence.findTopByProductIdOrderByVersionDesc(productCurrentStockDto.id)
            .orThrow { InventoryNotFound(productCurrentStockDto.id) }
        val changedInventory = inventory.changeStock(productCurrentStockDto.action, productCurrentStockDto.amount)
        val savedInventory: Inventory = inventoryPersistence.save(changedInventory)
        return InventoryDto(savedInventory.quantity)
    }

    @CachePut("inventory", key = "#productDto.id")
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