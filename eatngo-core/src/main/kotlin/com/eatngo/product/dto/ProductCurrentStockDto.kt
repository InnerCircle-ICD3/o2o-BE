package com.eatngo.product.dto

import com.eatngo.inventory.dto.InventoryDto
import com.eatngo.product.domain.Product

data class ProductCurrentStockDto(
    val id: Long,
    val action: String,
    val amount: Int
)

data class ProductAfterStockDto(
    val id: Long,
    val storeId: Long,
    val quantity: Int,
    val stock: Int,
) {
    companion object {
        fun create(product: Product, inventoryDto: InventoryDto): ProductAfterStockDto {
            val productId = requireNotNull(product.id) { "productId 가 비어있습니다." }
            val storeId = requireNotNull(product.storeId) { "product의 storeId가 비어있습니다." }

            return ProductAfterStockDto(
                id = productId,
                storeId = storeId,
                quantity = inventoryDto.quantity,
                stock = inventoryDto.stock
            )
        }
    }
}