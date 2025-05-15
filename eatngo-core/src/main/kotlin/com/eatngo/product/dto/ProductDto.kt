package com.eatngo.product.dto

import com.eatngo.product.domain.Inventory
import com.eatngo.product.domain.Product
import com.eatngo.product.domain.ProductPrice
import java.time.ZonedDateTime

data class ProductDto(
    val id: Long?,
    val name: String,
    val description: String,
    val size: String,
    val inventory: ProductInventoryDto,
    val price: ProductPriceDto,
    val imageUrl: String?,
    val storeId: Long,
    val foodTypes: List<String>,
    val status: String?,
    val createdAt: ZonedDateTime?,
    val updatedAt: ZonedDateTime?,
) {
    companion object {
        fun from(product: Product): ProductDto {
            val size = when (product) {
                is Product.LargeLuckBag -> "L"
                is Product.MediumLuckBag -> "M"
                is Product.SmallLuckBag -> "S"
            }
            return ProductDto(
                id = product.id,
                name = product.name,
                description = product.description,
                size = size,
                inventory = ProductInventoryDto.from(product.inventory),
                price = ProductPriceDto.from(product.price),
                imageUrl = product.imageUrl,
                storeId = product.storeId,
                foodTypes = product.foodTypes.foods.map { it.name },
                status = product.status.name,
                createdAt = product.createdAt,
                updatedAt = product.updatedAt,
            )
        }
    }
}

data class ProductInventoryDto(
    val quantity: Int,
    val stock: Int = quantity,
) {
    companion object {
        fun from(inventory: Inventory): ProductInventoryDto {
            return ProductInventoryDto(
                quantity = inventory.quantity,
                stock = inventory.stock,
            )
        }
    }
}

data class ProductPriceDto(
    val originalPrice: Int,
    val discountRate: Double = 0.5,
    val finalPrice: Int = (originalPrice * (1 - discountRate)).toInt()
) {
    companion object {
        fun from(price: ProductPrice): ProductPriceDto {
            return ProductPriceDto(
                originalPrice = price.originalPrice,
                discountRate = price.discountRate,
                finalPrice = price.finalPrice
            )
        }
    }
}