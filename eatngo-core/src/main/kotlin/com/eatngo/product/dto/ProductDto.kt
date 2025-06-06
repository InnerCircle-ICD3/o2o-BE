package com.eatngo.product.dto

import com.eatngo.inventory.dto.InventoryDto
import com.eatngo.product.domain.Product
import com.eatngo.product.domain.ProductPrice
import com.eatngo.store.vo.StoreNameVO
import java.time.LocalDateTime

data class ProductDto(
    var id: Long? = null,
    val name: String,
    val description: String,
    val size: String,
    val inventory: InventoryDto,
    val price: ProductPriceDto,
    var imageUrl: String? = null,
    val storeId: Long,
    val storeName: String? = null,
    val foodTypes: List<String>,
    var status: String? = "ACTIVE",
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null,
) {
    companion object {
        fun from(
            product: Product,
            imageUrl: String?,
            inventoryDto: InventoryDto,
            storeName: String?,
        ): ProductDto =
            ProductDto(
                id = product.id,
                name = product.name,
                description = product.description,
                // when 이 아닌 추상메소드로 로직 수정
                size = product.getSize().value,
                inventory = inventoryDto,
                price = ProductPriceDto.from(product.price),
                imageUrl = imageUrl,
                storeId = product.storeId,
                storeName = storeName,
                foodTypes = product.foodTypes.foods.map { it.name },
                status = product.status.name,
                createdAt = product.createdAt,
                updatedAt = product.updatedAt,
            )
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