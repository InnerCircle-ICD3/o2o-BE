package com.eatngo.product.dto

import com.eatngo.inventory.dto.InventoryDto
import java.time.LocalDateTime

data class UpdateProductResponseDto(
    val id: Long,
    val name: String,
    val description: String,
    val storeId: Long,
    val storeName: String,
    val price: UpdateProductPriceResponseDto,
    val size: String,
    val inventory: UpdateProductInventoryResponseDto,
    val foodType: List<String>,
    val status: String?,
    val image: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun from(productDto: ProductDto): UpdateProductResponseDto {
            return UpdateProductResponseDto(
                id = productDto.id!!,
                name = productDto.name,
                description = productDto.description,
                storeId = productDto.storeId,
                storeName = productDto.storeName ?: "Unknown Store",
                price = UpdateProductPriceResponseDto.from(productDto.price),
                size = productDto.size,
                inventory = UpdateProductInventoryResponseDto.from(productDto.inventory),
                foodType = productDto.foodTypes,
                status = productDto.status,
                image = productDto.imageUrl,
                createdAt = productDto.createdAt,
                updatedAt = productDto.updatedAt
            )
        }
    }
}

data class UpdateProductInventoryResponseDto(
    val quantity: Int,
    val stock: Int,
) {
    companion object {
        fun from(inventoryDto: InventoryDto): UpdateProductInventoryResponseDto {
            return UpdateProductInventoryResponseDto(
                quantity = inventoryDto.quantity,
                stock = inventoryDto.stock,
            )
        }
    }
}

data class UpdateProductPriceResponseDto(
    val originalPrice: Int,
    val discountRate: Double,
    val finalPrice: Int
) {
    companion object {
        fun from(priceDto: ProductPriceDto): UpdateProductPriceResponseDto {
            return UpdateProductPriceResponseDto(
                originalPrice = priceDto.originalPrice,
                discountRate = priceDto.discountRate,
                finalPrice = priceDto.finalPrice
            )
        }
    }
}