package com.eatngo.product.dto

data class ToggleStockResponseDto(
    val id: Long,
    val storeId: Long,
    val quantity: Int,
    val stock: Int
) {
    companion object {
        fun from(productAfterStockDto: ProductAfterStockDto): ToggleStockResponseDto {
            return ToggleStockResponseDto(
                id = productAfterStockDto.id,
                storeId = productAfterStockDto.storeId,
                quantity = productAfterStockDto.quantity,
                stock = productAfterStockDto.stock
            )
        }
    }
}
