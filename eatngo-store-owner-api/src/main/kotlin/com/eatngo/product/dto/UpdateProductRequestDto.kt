package com.eatngo.product.dto

import java.time.ZonedDateTime

data class UpdateProductRequestDto(
    val name: String,
    val description: String,
    val price: UpdateProductPriceRequestDto,
    val size: String,
    val inventory: UpdateProductInventoryRequestDto,
    val foodType: List<String>,
    val image: String?,
    val status: String?,
    val createdAt: ZonedDateTime?,
    val updatedAt: ZonedDateTime?
) {
    companion object {
        fun from(productDto: ProductDto): UpdateProductRequestDto {
            return UpdateProductRequestDto(
                name = productDto.name,
                description = productDto.description,
                price = UpdateProductPriceRequestDto.from(productDto.price),
                size = productDto.size,
                inventory = UpdateProductInventoryRequestDto.from(productDto.inventory),
                foodType = productDto.foodTypes,
                image = productDto.imageUrl,
                status = productDto.status,
                createdAt = productDto.createdAt,
                updatedAt = productDto.updatedAt
            )
        }
    }
}

data class UpdateProductInventoryRequestDto(
    val quantity: Int,
    val stock: Int,
) {
    companion object {
        fun from(inventoryDto: ProductInventoryDto): UpdateProductInventoryRequestDto {
            return UpdateProductInventoryRequestDto(
                quantity = inventoryDto.quantity,
                stock = inventoryDto.stock,
            )
        }
    }
}

data class UpdateProductPriceRequestDto(
    val originalPrice: Int,
    val discountRate: Double,
    val finalPrice: Int
) {
    companion object {
        fun from(priceDto: ProductPriceDto): UpdateProductPriceRequestDto {
            return UpdateProductPriceRequestDto(
                originalPrice = priceDto.originalPrice,
                discountRate = priceDto.discountRate,
                finalPrice = priceDto.finalPrice
            )
        }
    }
}