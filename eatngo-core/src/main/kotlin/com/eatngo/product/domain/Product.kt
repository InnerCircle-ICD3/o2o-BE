package com.eatngo.product.domain

import com.eatngo.product.domain.ProductSizeType.*
import java.time.LocalDateTime

sealed class Product {
    abstract var id: Long
    abstract var name: String
    abstract var description: String
    abstract var price: ProductPrice
    abstract var imageUrl: String?
    abstract val storeId: Long
    abstract val foodTypes: FoodTypes
    abstract var status: ProductStatus
    abstract var deletedStatus: DeletedStatus
    abstract var createdAt: LocalDateTime?
    abstract var updatedAt: LocalDateTime?

    abstract fun getSize(): ProductSizeType

    abstract fun modify(
        name: String,
        description: String,
        price: ProductPrice,
        imageUrl: String?,
        foodTypes: FoodTypes,
        status: ProductStatus,
    )

    abstract fun remove()

    data class LargeEatNGoBag(
        override var id: Long = 0,
        override var name: String,
        override var description: String,
        override var price: ProductPrice,
        override var imageUrl: String?,
        override val storeId: Long,
        override var foodTypes: FoodTypes,
        override var status: ProductStatus = ProductStatus.ACTIVE,
        override var deletedStatus: DeletedStatus = DeletedStatus.ACTIVE,
        override var createdAt: LocalDateTime? = null,
        override var updatedAt: LocalDateTime? = null,
    ) : Product() {
        override fun getSize() = L

        override fun modify(
            name: String,
            description: String,
            price: ProductPrice,
            imageUrl: String?,
            foodTypes: FoodTypes,
            status: ProductStatus,
        ) {
            this.name = name
            this.description = description
            this.price = ProductPrice(
                originalPrice = price.originalPrice,
                discountRate = price.discountRate,
            )
            this.imageUrl = imageUrl
            this.foodTypes = foodTypes
            this.status = status
        }

        override fun remove() {
            this.deletedStatus = DeletedStatus.DELETED
        }
    }

    data class MediumEatNGoBag(
        override var id: Long = 0,
        override var name: String,
        override var description: String,
        override var price: ProductPrice,
        override var imageUrl: String?,
        override val storeId: Long,
        override var foodTypes: FoodTypes,
        override var status: ProductStatus = ProductStatus.ACTIVE,
        override var deletedStatus: DeletedStatus = DeletedStatus.ACTIVE,
        override var createdAt: LocalDateTime? = null,
        override var updatedAt: LocalDateTime? = null,
    ) : Product() {
        override fun getSize() = M

        override fun modify(
            name: String,
            description: String,
            price: ProductPrice,
            imageUrl: String?,
            foodTypes: FoodTypes,
            status: ProductStatus,
        ) {
            this.name = name
            this.description = description
            this.price = ProductPrice(
                originalPrice = price.originalPrice,
                discountRate = price.discountRate,
            )
            this.imageUrl = imageUrl
            this.foodTypes = foodTypes
            this.status = status
        }

        override fun remove() {
            this.deletedStatus = DeletedStatus.DELETED
        }
    }

    data class SmallEatNGoBag(
        override var id: Long = 0,
        override var name: String,
        override var description: String,
        override var price: ProductPrice,
        override var imageUrl: String?,
        override val storeId: Long,
        override var foodTypes: FoodTypes,
        override var status: ProductStatus = ProductStatus.ACTIVE,
        override var deletedStatus: DeletedStatus = DeletedStatus.ACTIVE,
        override var createdAt: LocalDateTime? = null,
        override var updatedAt: LocalDateTime? = null,
    ) : Product() {
        override fun getSize() = S

        override fun modify(
            name: String,
            description: String,
            price: ProductPrice,
            imageUrl: String?,
            foodTypes: FoodTypes,
            status: ProductStatus,
        ) {
            this.name = name
            this.description = description
            this.price = ProductPrice(
                originalPrice = price.originalPrice,
                discountRate = price.discountRate,
            )
            this.imageUrl = imageUrl
            this.foodTypes = foodTypes
            this.status = status
        }

        override fun remove() {
            this.deletedStatus = DeletedStatus.DELETED
        }
    }
}