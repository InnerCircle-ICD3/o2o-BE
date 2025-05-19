package com.eatngo.product.domain

import com.eatngo.product.domain.ProductSizeType.*
import com.eatngo.product.domain.StockActionType.DECREASE
import com.eatngo.product.domain.StockActionType.INCREASE
import java.time.LocalDateTime

sealed class Product {
    abstract var id: Long?
    abstract var name: String
    abstract var description: String
    abstract var inventory: Inventory
    abstract var price: ProductPrice
    abstract var imageUrl: String?
    abstract val storeId: Long
    abstract var foodTypes: FoodTypes
    abstract var status: ProductStatus
    abstract var createdAt: LocalDateTime?
    abstract var updatedAt: LocalDateTime?

    abstract fun getSize(): ProductSizeType

    abstract fun changeStock(
        action: String,
        amount: Int
    )

    abstract fun modify(
        name: String,
        description: String,
        inventory: Inventory,
        price: ProductPrice,
        imageUrl: String?,
        foodTypes: FoodTypes,
        status: ProductStatus,
    )

    data class LargeEatNGoBag(
        override var id: Long? = null,
        override var name: String,
        override var description: String,
        override var inventory: Inventory,
        override var price: ProductPrice,
        override var imageUrl: String?,
        override var storeId: Long,
        override var foodTypes: FoodTypes,
        override var status: ProductStatus = ProductStatus.ACTIVE,
        override var createdAt: LocalDateTime? = null,
        override var updatedAt: LocalDateTime? = null,
    ) : Product() {
        override fun getSize() = L

        override fun changeStock(
            action: String,
            amount: Int
        ) {
            val changedInventory: Inventory = when (StockActionType.fromValue(action)) {
                INCREASE -> inventory.increaseStock(amount)
                DECREASE -> inventory.decreaseStock(amount)
            }
            this.inventory = changedInventory
        }

        override fun modify(
            name: String,
            description: String,
            inventory: Inventory,
            price: ProductPrice,
            imageUrl: String?,
            foodTypes: FoodTypes,
            status: ProductStatus,
        ) {
            this.name = name
            this.description = description
            this.inventory = Inventory(
                quantity = inventory.quantity,
                stock = inventory.stock
            )
            this.price = ProductPrice(
                originalPrice = price.originalPrice,
                discountRate = price.discountRate,
            )
            this.imageUrl = imageUrl
            this.foodTypes = foodTypes
            this.status = status
        }
    }

    data class MediumEatNGoBag(
        override var id: Long? = null,
        override var name: String,
        override var description: String,
        override var inventory: Inventory,
        override var price: ProductPrice,
        override var imageUrl: String?,
        override val storeId: Long,
        override var foodTypes: FoodTypes,
        override var status: ProductStatus = ProductStatus.ACTIVE,
        override var createdAt: LocalDateTime? = null,
        override var updatedAt: LocalDateTime? = null,
    ) : Product() {
        override fun getSize() = M

        override fun changeStock(
            action: String,
            amount: Int
        ) {
            val changedInventory: Inventory = when (StockActionType.fromValue(action)) {
                INCREASE -> inventory.increaseStock(amount)
                DECREASE -> inventory.decreaseStock(amount)
            }
            this.inventory = changedInventory
        }

        override fun modify(
            name: String,
            description: String,
            inventory: Inventory,
            price: ProductPrice,
            imageUrl: String?,
            foodTypes: FoodTypes,
            status: ProductStatus,
        ) {
            this.name = name
            this.description = description
            this.inventory = Inventory(
                quantity = inventory.quantity,
                stock = inventory.stock
            )
            this.price = ProductPrice(
                originalPrice = price.originalPrice,
                discountRate = price.discountRate,
            )
            this.imageUrl = imageUrl
            this.foodTypes = foodTypes
            this.status = status
        }
    }

    data class SmallEatNGoBag(
        override var id: Long? = null,
        override var name: String,
        override var description: String,
        override var inventory: Inventory,
        override var price: ProductPrice,
        override var imageUrl: String?,
        override val storeId: Long,
        override var foodTypes: FoodTypes,
        override var status: ProductStatus = ProductStatus.ACTIVE,
        override var createdAt: LocalDateTime? = null,
        override var updatedAt: LocalDateTime? = null,
    ) : Product() {
        override fun getSize() = S

        override fun changeStock(
            action: String,
            amount: Int
        ) {
            val changedInventory: Inventory = when (StockActionType.fromValue(action)) {
                INCREASE -> inventory.increaseStock(amount)
                DECREASE -> inventory.decreaseStock(amount)
            }
            this.inventory = changedInventory
        }

        override fun modify(
            name: String,
            description: String,
            inventory: Inventory,
            price: ProductPrice,
            imageUrl: String?,
            foodTypes: FoodTypes,
            status: ProductStatus,
        ) {
            this.name = name
            this.description = description
            this.inventory = Inventory(
                quantity = inventory.quantity,
                stock = inventory.stock
            )
            this.price = ProductPrice(
                originalPrice = price.originalPrice,
                discountRate = price.discountRate,
            )
            this.imageUrl =imageUrl
            this.foodTypes = foodTypes
            this.status = status
        }
    }
}