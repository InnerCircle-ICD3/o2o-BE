package com.eatngo.inventory.service

import com.eatngo.common.exception.InventoryException.InventoryNotFound
import com.eatngo.extension.orThrow
import com.eatngo.inventory.domain.Inventory
import com.eatngo.inventory.dto.InventoryDto
import com.eatngo.inventory.infra.InventoryPersistence
import com.eatngo.product.dto.ProductCurrentStockDto
import com.eatngo.product.dto.ProductDto
import com.eatngo.product.service.StoreTotalInventoryTypeDecider
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InventoryService(
    private val inventoryPersistence: InventoryPersistence,
    private val storeTotalInventoryTypeDecider: StoreTotalInventoryTypeDecider
) {

    @CachePut("inventory", key = "#productDto.id")
    @Transactional
    fun createInventory(productDto: ProductDto): InventoryDto {
        val inventory = Inventory.create(productDto.inventory.quantity, productDto.id!!)
        val savedInventory: Inventory = inventoryPersistence.save(inventory)
        return InventoryDto(savedInventory.quantity, savedInventory.stock)
    }

    @Cacheable("inventory", key = "#productId")
    fun getInventoryDetails(productId: Long): InventoryDto {
        val inventory: Inventory =
            inventoryPersistence.findTopByProductIdOrderByVersionDesc(productId)
                .orThrow { InventoryNotFound(productId) }
        return InventoryDto(inventory.quantity, inventory.stock)
    }

    @CacheEvict("inventory", key = "#productId")
    @Transactional
    fun deleteInventory(productId: Long) {
        inventoryPersistence.deleteByProductId(productId)
    }

    @CachePut("inventory", key = "#productCurrentStockDto.id")
    @Transactional
    fun toggleInventory(
        productCurrentStockDto: ProductCurrentStockDto,
        storeId: Long,
        initialStock: Int
    ): InventoryDto {
        val inventory: Inventory = inventoryPersistence.findTopByProductIdOrderByVersionDesc(productCurrentStockDto.id)
            .orThrow { InventoryNotFound(productCurrentStockDto.id) }
        val changedInventory = inventory.changeStock(productCurrentStockDto.action, productCurrentStockDto.amount)
        val savedInventory: Inventory = inventoryPersistence.save(changedInventory)

        storeTotalInventoryTypeDecider.decideInventoryType(
            storeId = storeId,
            initialStock = initialStock,
        )

        return InventoryDto(savedInventory.quantity, savedInventory.stock)
    }

    @CachePut("inventory", key = "#productDto.id")
    @Transactional
    fun modifyInventory(productDto: ProductDto, initialStock: Int): InventoryDto {
        val inventory: Inventory = inventoryPersistence.findTopByProductIdOrderByVersionDesc(productDto.id!!)
            .orThrow { InventoryNotFound(productDto.id!!) }

        inventory.modify(
            quantity = productDto.inventory.quantity,
            stock = productDto.inventory.stock,
        )

        val savedInventory = inventoryPersistence.save(inventory)

        storeTotalInventoryTypeDecider.decideInventoryType(
            storeId = productDto.storeId,
            initialStock = initialStock
        )

        return InventoryDto(savedInventory.quantity, savedInventory.stock)
    }

}