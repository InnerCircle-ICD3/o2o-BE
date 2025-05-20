package com.eatngo.product.dto

import java.time.LocalDateTime

data class CreateProductResponseDto(
    val id: Long?,
    val name: String,
    val storeId: Long,
    val price: CreateProductPriceResponseDto,
    val size: String,
    val inventory: CreateProductInventoryResponseDto,
    val foodType: List<String>,
    val status: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun from(productDto: ProductDto): CreateProductResponseDto {
            return CreateProductResponseDto(
                id = productDto.id,
                name = productDto.name,
                storeId = productDto.storeId,
                price = CreateProductPriceResponseDto.from(productDto.price),
                size = productDto.size,
                inventory = CreateProductInventoryResponseDto.from(productDto.inventory),
                foodType = productDto.foodTypes,
                status = productDto.status,
                createdAt = productDto.createdAt,
                updatedAt = productDto.updatedAt
            )
        }
    }
}

data class CreateProductInventoryResponseDto(
    val quantity: Int,
    val stock: Int,
) {
    companion object {
        fun from(inventoryDto: ProductInventoryDto): CreateProductInventoryResponseDto {
            return CreateProductInventoryResponseDto(
                quantity = inventoryDto.quantity,
                stock = inventoryDto.stock,
            )
        }
    }
}

data class CreateProductPriceResponseDto(
    val originalPrice: Int,
    val discountRate: Double,
    val finalPrice: Int
) {
    companion object {
        fun from(priceDto: ProductPriceDto): CreateProductPriceResponseDto {
            return CreateProductPriceResponseDto(
                originalPrice = priceDto.originalPrice,
                discountRate = priceDto.discountRate,
                finalPrice = priceDto.finalPrice
            )
        }
    }
}