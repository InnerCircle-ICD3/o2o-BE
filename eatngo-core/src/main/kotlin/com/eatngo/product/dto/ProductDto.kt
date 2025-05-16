package com.eatngo.product.dto

import com.eatngo.product.domain.Inventory
import com.eatngo.product.domain.Product
import com.eatngo.product.domain.ProductPrice
import java.time.ZonedDateTime

data class ProductDto(
    var id: Long? = null,
    val name: String,
    val description: String,
    val size: String,
    val inventory: ProductInventoryDto,
    val price: ProductPriceDto,
    var imageUrl: String? = null,
    val storeId: Long,
    val foodTypes: List<String>,
    var status: String? = "ACTIVE",
    var createdAt: ZonedDateTime? = null,
    var updatedAt: ZonedDateTime? = null,
) {
    companion object {
        fun from(product: Product): ProductDto {
//            val size = when (product) {
//                is Product.LargeLuckBag -> "L"
//                is Product.MediumLuckBag -> "M"
//                is Product.SmallLuckBag -> "S"
//            }
            return ProductDto(
                id = product.id,
                name = product.name,
                description = product.description,
                // when 이 아닌 추상메소드로 로직 수정
                size = product.getSize(),
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