package com.eatngo.product.service

import com.eatngo.common.exception.product.InventoryException.InventoryNotFound
import com.eatngo.extension.orThrow
import com.eatngo.inventory.event.InventoryChangedEvent
import com.eatngo.inventory.event.InventoryChangedType
import com.eatngo.inventory.event.InventoryChangedType.*
import com.eatngo.inventory.event.InventoryEventPublisher
import com.eatngo.inventory.infra.InventoryPersistence
import com.eatngo.product.domain.Product
import com.eatngo.product.infra.ProductPersistence
import org.springframework.stereotype.Service

@Service
class InventoryChangeNotifier(
    private val inventoryPersistence: InventoryPersistence,
    private val productPersistence: ProductPersistence,
    private val inventoryEventPublisher: InventoryEventPublisher
) {

    companion object {
        private const val LOW_STOCK_THRESHOLD = 3
        private const val IN_STOCK_THRESHOLD = 5
    }

    fun notifyInventoryStatusChange(storeId: Long, productId: Long, initialStock: Int) {
        inventoryEventPublisher.publishInventoryChangedEvent(
            InventoryChangedEvent(
                productId = productId,
                inventoryChangedType = determineInventoryChangedType(storeId, initialStock),
                previousTotalStock = initialStock,
                afterTotalStock = calculateCurrentTotalStock(productId)
            )
        )
    }

    fun determineInventoryChangedType(storeId: Long, initialStock: Int): InventoryChangedType {
        val totalStockQuantity = calculateCurrentTotalStock(storeId)

        if (initialStock == 0 && totalStockQuantity > 0) {
            return RESTOCKED
        }

        return when {
            totalStockQuantity == 0 -> OUT_OF_STOCK
            totalStockQuantity <= LOW_STOCK_THRESHOLD -> LOW_STOCK
            totalStockQuantity >= IN_STOCK_THRESHOLD -> IN_STOCK
            else -> ADEQUATE_STOCK
        }
    }

    private fun calculateCurrentTotalStock(storeId: Long): Int {
        val allProducts: List<Product> = productPersistence.findAllActivatedProductByStoreId(storeId)

        val totalStockQuantity = allProducts.asSequence()
            .map { product ->
                inventoryPersistence.findTopByProductIdOrderByVersionDesc(product.id)
                    .orThrow { InventoryNotFound(product.id) }
                    .stock
            }
            .sum()
        return totalStockQuantity
    }
}