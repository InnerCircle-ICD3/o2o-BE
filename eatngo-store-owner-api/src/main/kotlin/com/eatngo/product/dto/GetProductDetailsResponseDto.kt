package com.eatngo.product.dto

import java.time.LocalDateTime

data class GetProductDetailsResponseDto(
    val id: Long?,
    val name: String,
    val storeId: Long,
    val price: GetProductPriceResponseDto,
    val size: String,
    val inventory: GetProductInventoryResponseDto,
    val foodType: List<String>,
    val status: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun from(productDto: ProductDto): GetProductDetailsResponseDto {
            return GetProductDetailsResponseDto(
                id = productDto.id,
                name = productDto.name,
                storeId = productDto.storeId,
                price = GetProductPriceResponseDto.from(productDto.price),
                size = productDto.size,
                inventory = GetProductInventoryResponseDto.from(productDto.inventory),
                foodType = productDto.foodTypes,
                status = productDto.status,
                createdAt = productDto.createdAt,
                updatedAt = productDto.updatedAt
            )
        }
    }
}

data class GetProductInventoryResponseDto(
    val quantity: Int,
    val stock: Int,
) {
    companion object {
        fun from(inventoryDto: ProductInventoryDto): GetProductInventoryResponseDto {
            return GetProductInventoryResponseDto(
                quantity = inventoryDto.quantity,
                stock = inventoryDto.stock,
            )
        }
    }
}

data class GetProductPriceResponseDto(
    val originalPrice: Int,
    val discountRate: Double,
    val finalPrice: Int
) {
    companion object {
        fun from(priceDto: ProductPriceDto): GetProductPriceResponseDto {
            return GetProductPriceResponseDto(
                originalPrice = priceDto.originalPrice,
                discountRate = priceDto.discountRate,
                finalPrice = priceDto.finalPrice
            )
        }
    }
}