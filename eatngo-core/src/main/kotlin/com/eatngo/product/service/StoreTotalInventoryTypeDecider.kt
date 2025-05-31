package com.eatngo.product.service

import com.eatngo.common.exception.InventoryException.InventoryNotFound
import com.eatngo.extension.orThrow
import com.eatngo.inventory.event.InventoryChangedType
import com.eatngo.inventory.infra.InventoryPersistence
import com.eatngo.product.domain.Product
import com.eatngo.product.infra.ProductPersistence
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class StoreTotalInventoryTypeDecider(
    private val inventoryPersistence: InventoryPersistence,
    private val productPersistence: ProductPersistence
) {

    companion object {
        private const val LOW_STOCK_THRESHOLD = 3
        private const val IN_STOCK_THRESHOLD = 5
    }

    @Cacheable("storeProducts", key = "#storeId")
    fun decideInventoryType(storeId: Long): InventoryChangedType {
        val allProducts: List<Product> = productPersistence.findAllByStoreId(storeId)

        val totalStockQuantity = allProducts.asSequence()
            .map { product ->
                inventoryPersistence.findTopByProductIdOrderByVersionDesc(product.id)
                    .orThrow { InventoryNotFound(product.id) }
                    .stock
            }
            .sum()

        return when {
            totalStockQuantity == 0 -> InventoryChangedType.OUT_OF_STOCK
            totalStockQuantity <= LOW_STOCK_THRESHOLD -> InventoryChangedType.LOW_STOCK
            totalStockQuantity >= IN_STOCK_THRESHOLD -> InventoryChangedType.IN_STOCK
            else -> InventoryChangedType.ADEQUATE_STOCK
        }
    }

}