package com.eatngo.product.domain

import java.time.ZonedDateTime

sealed class Product {
    abstract var id: Long?
    abstract val name: String
    abstract val description: String
    abstract val inventory: Inventory
    abstract val price: ProductPrice
    abstract val imageUrl: String?
    abstract val storeId: Long
    abstract val foodTypes: FoodTypes
    abstract val status: ProductStatus
    abstract val createdAt: ZonedDateTime?
    abstract val updatedAt: ZonedDateTime?

    abstract fun getSize(): ProductSizeType

    data class LargeEatNGoBag(
        override var id: Long?,
        override val name: String,
        override val description: String,
        override val inventory: Inventory,
        override val price: ProductPrice,
        override val imageUrl: String?,
        override val storeId: Long,
        override val foodTypes: FoodTypes,
        override val status: ProductStatus = ProductStatus.ACTIVE,
        override val createdAt: ZonedDateTime? = null,
        override val updatedAt: ZonedDateTime? = null,
    ) : Product() {
        override fun getSize() = ProductSizeType.L
    }

    data class MediumEatNGoBag(
        override var id: Long?,
        override val name: String,
        override val description: String,
        override val inventory: Inventory,
        override val price: ProductPrice,
        override val imageUrl: String?,
        override val storeId: Long,
        override val foodTypes: FoodTypes,
        override val status: ProductStatus = ProductStatus.ACTIVE,
        override val createdAt: ZonedDateTime? = null,
        override val updatedAt: ZonedDateTime? = null,
    ) : Product() {
        override fun getSize() = ProductSizeType.M
    }

    data class SmallEatNGoBag(
        override var id: Long?,
        override val name: String,
        override val description: String,
        override val inventory: Inventory,
        override val price: ProductPrice,
        override val imageUrl: String?,
        override val storeId: Long,
        override val foodTypes: FoodTypes,
        override val status: ProductStatus = ProductStatus.ACTIVE,
        override val createdAt: ZonedDateTime? = null,
        override val updatedAt: ZonedDateTime? = null,
    ) : Product() {
        override fun getSize() = ProductSizeType.S
    }
}