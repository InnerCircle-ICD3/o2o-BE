package com.eatngo.product.domain

import com.eatngo.product.domain.ProductSizeType.*
import com.eatngo.product.domain.StockActionType.DECREASE
import com.eatngo.product.domain.StockActionType.INCREASE
import java.time.ZonedDateTime

sealed class Product {
    abstract var id: Long?
    abstract val name: String
    abstract val description: String
    abstract var inventory: Inventory
    abstract val price: ProductPrice
    abstract val imageUrl: String?
    abstract val storeId: Long
    abstract val foodTypes: FoodTypes
    abstract val status: ProductStatus
    abstract val createdAt: ZonedDateTime?
    abstract val updatedAt: ZonedDateTime?

    abstract fun getSize(): ProductSizeType

    abstract fun changeStock(
        action: String,
        amount: Int
    )

    data class LargeEatNGoBag(
        override var id: Long? = null,
        override val name: String,
        override val description: String,
        override var inventory: Inventory,
        override val price: ProductPrice,
        override val imageUrl: String?,
        override val storeId: Long,
        override val foodTypes: FoodTypes,
        override val status: ProductStatus = ProductStatus.ACTIVE,
        override val createdAt: ZonedDateTime? = null,
        override val updatedAt: ZonedDateTime? = null,
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
    }

    data class MediumEatNGoBag(
        override var id: Long? = null,
        override val name: String,
        override val description: String,
        override var inventory: Inventory,
        override val price: ProductPrice,
        override val imageUrl: String?,
        override val storeId: Long,
        override val foodTypes: FoodTypes,
        override val status: ProductStatus = ProductStatus.ACTIVE,
        override val createdAt: ZonedDateTime? = null,
        override val updatedAt: ZonedDateTime? = null,
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
    }

    data class SmallEatNGoBag(
        override var id: Long? = null,
        override val name: String,
        override val description: String,
        override var inventory: Inventory,
        override val price: ProductPrice,
        override val imageUrl: String?,
        override val storeId: Long,
        override val foodTypes: FoodTypes,
        override val status: ProductStatus = ProductStatus.ACTIVE,
        override val createdAt: ZonedDateTime? = null,
        override val updatedAt: ZonedDateTime? = null,
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
    }
}