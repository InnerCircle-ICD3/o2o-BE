package com.eatngo.inventory.service

import com.eatngo.extension.orElse
import com.eatngo.inventory.domain.Inventory
import com.eatngo.inventory.dto.InventoryDto
import com.eatngo.inventory.event.InventorySyncEvent
import com.eatngo.inventory.event.InventorySyncPublisher
import com.eatngo.inventory.infra.InventoryCachePersistence
import com.eatngo.inventory.infra.InventoryPersistence
import com.eatngo.product.dto.ProductCurrentStockDto
import com.eatngo.product.dto.ProductDto
import com.eatngo.product.service.InventoryChangeNotifier
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class InventoryService(
    private val inventoryPersistence: InventoryPersistence,
    private val inventoryChangeNotifier: InventoryChangeNotifier,
    private val inventorySyncPublisher: InventorySyncPublisher,
    private val inventoryCachePersistence: InventoryCachePersistence
) {

    @CachePut("inventory", key = "#productDto.id")
    @Transactional
    fun createInventory(productDto: ProductDto): InventoryDto {
        val inventory = Inventory.create(productDto.inventory.quantity, productDto.id!!)
        val savedInventory: Inventory = inventoryPersistence.save(inventory)
        val inventoryDto: InventoryDto = InventoryDto(savedInventory.quantity, savedInventory.stock)
        inventoryCachePersistence.saveHash(productDto.id!!, inventoryDto)
        return inventoryDto
    }

    @Cacheable("inventory", key = "#productId")
    fun getInventoryDetails(productId: Long): InventoryDto {
        val inventory: Inventory =
            inventoryPersistence.findTopByProductIdOrderByVersionDesc(productId, LocalDate.now())
                .orElse {
                    inventoryPersistence.save(
                        Inventory(
                            quantity = 0,
                            stock = 0,
                            productId = productId,
                            inventoryDate = LocalDate.now(),
                        )
                    )
                }
        return InventoryDto(inventory.quantity, inventory.stock)
    }

    @CacheEvict("inventory", key = "#productId")
    @Transactional
    fun deleteInventory(productId: Long) {
        inventoryPersistence.deleteByProductId(productId)
        inventoryCachePersistence.deleteHash(productId)
    }

    @CachePut("inventory", key = "#productCurrentStockDto.id")
    @Transactional
    fun toggleInventory(
        productCurrentStockDto: ProductCurrentStockDto,
        storeId: Long,
        initialStock: Int
    ): InventoryDto {
        val inventory: Inventory =
            inventoryPersistence.findTopByProductIdOrderByVersionDesc(productCurrentStockDto.id, LocalDate.now())
                .orElse {
                    inventoryPersistence.save(
                        Inventory(
                            quantity = 0,
                            stock = 0,
                            productId = productCurrentStockDto.id,
                            inventoryDate = LocalDate.now(),
                        )
                    )
                }
        val changedInventory = inventory.changeStock(productCurrentStockDto.action, productCurrentStockDto.amount)
        val savedInventory: Inventory = inventoryPersistence.save(changedInventory)

        inventoryChangeNotifier.notifyInventoryStatusChange(
            storeId = storeId,
            productId = productCurrentStockDto.id,
            initialStock = initialStock,
        )

        inventorySyncPublisher.publishEvent(
            InventorySyncEvent(
                productId = savedInventory.productId,
                syncedStock = savedInventory.stock
            )
        )

        val inventoryDto: InventoryDto = InventoryDto(savedInventory.quantity, savedInventory.stock)
        inventoryCachePersistence.saveHash(productCurrentStockDto.id, inventoryDto)
        return inventoryDto
    }

    @CachePut("inventory", key = "#productDto.id")
    @Transactional
    fun modifyInventory(productDto: ProductDto, initialStock: Int): InventoryDto {
        val inventory: Inventory =
            inventoryPersistence.findTopByProductIdOrderByVersionDesc(productDto.id!!, LocalDate.now())
                .orElse {
                    inventoryPersistence.save(
                        Inventory(
                            quantity = 0,
                            stock = 0,
                            productId = productDto.id!!,
                            inventoryDate = LocalDate.now(),
                        )
                    )
                }

        inventory.modify(
            quantity = productDto.inventory.quantity,
            stock = productDto.inventory.stock,
        )

        val savedInventory = inventoryPersistence.save(inventory)

        inventoryChangeNotifier.notifyInventoryStatusChange(
            storeId = productDto.storeId,
            productId = savedInventory.productId,
            initialStock = initialStock
        )

        inventorySyncPublisher.publishEvent(
            InventorySyncEvent(
                productId = savedInventory.productId,
                syncedStock = savedInventory.stock
            )
        )

        val inventoryDto: InventoryDto = InventoryDto(savedInventory.quantity, savedInventory.stock)
        inventoryCachePersistence.saveHash(productDto.id!!, inventoryDto)
        return inventoryDto
    }

}