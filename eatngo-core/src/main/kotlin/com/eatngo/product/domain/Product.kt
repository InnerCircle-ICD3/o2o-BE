package com.eatngo.product.domain

import com.eatngo.product.domain.ProductSizeType.*
import java.time.LocalDateTime

sealed class Product {
    abstract val id: Long
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
        size: String,
    ): Product

    abstract fun remove()

    data class LargeEatNGoBag(
        override val id: Long = 0,
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
            size: String,
        ): Product {
            return when (ProductSizeType.fromValue(size)) {
                L -> LargeEatNGoBag(
                    id = id,
                    name = name,
                    description = description,
                    price = price,
                    imageUrl = imageUrl,
                    storeId = storeId,
                    foodTypes = foodTypes,
                    status = status,
                    deletedStatus = deletedStatus,
                    createdAt = createdAt,
                )

                M -> MediumEatNGoBag(
                    id = id,
                    name = name,
                    description = description,
                    price = price,
                    imageUrl = imageUrl,
                    storeId = storeId,
                    foodTypes = foodTypes,
                    status = status,
                    deletedStatus = deletedStatus,
                    createdAt = createdAt,
                )

                S -> SmallEatNGoBag(
                    id = id,
                    name = name,
                    description = description,
                    price = price,
                    imageUrl = imageUrl,
                    storeId = storeId,
                    foodTypes = foodTypes,
                    status = status,
                    deletedStatus = deletedStatus,
                    createdAt = createdAt,
                )
            }
        }

        override fun remove() {
            this.deletedStatus = DeletedStatus.DELETED
            this.status = ProductStatus.INACTIVE
        }
    }

    data class MediumEatNGoBag(
        override val id: Long = 0,
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
            size: String,
        ): Product {
            return when (ProductSizeType.fromValue(size)) {
                L -> LargeEatNGoBag(
                    id = id,
                    name = name,
                    description = description,
                    price = price,
                    imageUrl = imageUrl,
                    storeId = storeId,
                    foodTypes = foodTypes,
                    status = status,
                    deletedStatus = deletedStatus,
                    createdAt = createdAt,
                )

                M -> MediumEatNGoBag(
                    id = id,
                    name = name,
                    description = description,
                    price = price,
                    imageUrl = imageUrl,
                    storeId = storeId,
                    foodTypes = foodTypes,
                    status = status,
                    deletedStatus = deletedStatus,
                    createdAt = createdAt,
                )

                S -> SmallEatNGoBag(
                    id = id,
                    name = name,
                    description = description,
                    price = price,
                    imageUrl = imageUrl,
                    storeId = storeId,
                    foodTypes = foodTypes,
                    status = status,
                    deletedStatus = deletedStatus,
                    createdAt = createdAt,
                )
            }
        }

        override fun remove() {
            this.deletedStatus = DeletedStatus.DELETED
            this.status = ProductStatus.INACTIVE
        }
    }

    data class SmallEatNGoBag(
        override val id: Long = 0,
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
            size: String,
        ): Product {
            return when (ProductSizeType.fromValue(size)) {
                L -> LargeEatNGoBag(
                    id = id,
                    name = name,
                    description = description,
                    price = price,
                    imageUrl = imageUrl,
                    storeId = storeId,
                    foodTypes = foodTypes,
                    status = status,
                    deletedStatus = deletedStatus,
                    createdAt = createdAt,
                )

                M -> MediumEatNGoBag(
                    id = id,
                    name = name,
                    description = description,
                    price = price,
                    imageUrl = imageUrl,
                    storeId = storeId,
                    foodTypes = foodTypes,
                    status = status,
                    deletedStatus = deletedStatus,
                    createdAt = createdAt,
                )

                S -> SmallEatNGoBag(
                    id = id,
                    name = name,
                    description = description,
                    price = price,
                    imageUrl = imageUrl,
                    storeId = storeId,
                    foodTypes = foodTypes,
                    status = status,
                    deletedStatus = deletedStatus,
                    createdAt = createdAt,
                )
            }
        }

        override fun remove() {
            this.deletedStatus = DeletedStatus.DELETED
            this.status = ProductStatus.INACTIVE
        }
    }
}