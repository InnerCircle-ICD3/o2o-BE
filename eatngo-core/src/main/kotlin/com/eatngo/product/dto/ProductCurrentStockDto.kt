package com.eatngo.product.dto

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
        fun create(product: Product): ProductAfterStockDto {
            return ProductAfterStockDto(
                id = product.id!!,
                storeId = product.storeId,
                quantity = product.inventory.quantity,
                stock = product.inventory.stock
            )
        }
    }
}