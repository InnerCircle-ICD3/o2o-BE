package com.eatngo.inventory.domain

import com.eatngo.product.domain.StockActionType
import com.eatngo.product.domain.StockActionType.DECREASE
import com.eatngo.product.domain.StockActionType.INCREASE

data class Inventory(
    val id: Long = 0L,
    var quantity: Int, // 점주가 지정한 상품의 수량
    var stock: Int, // 현재 재고량
    val version: Long = 0L,
    val productId: Long,
) {
    fun changeStock(
        action: String,
        amount: Int
    ): Inventory {
        val changedInventory: Inventory = when (StockActionType.fromValue(action)) {
            INCREASE -> increaseStock(amount)
            DECREASE -> decreaseStock(amount)
        }
        return changedInventory
    }

    fun changeInventory(
        quantity: Int = this.quantity,
        stock: Int,
    ): Inventory {
        return Inventory(
            id = this.id,
            quantity = quantity,
            stock = stock,
            productId = this.productId
        )
    }

    fun modify(
        quantity: Int = this.quantity,
        stock: Int,
    ) {
        this.quantity = quantity
        this.stock = stock
    }

    private fun increaseStock(amount: Int): Inventory {
        return Inventory(this.id, this.quantity, this.stock + amount, this.version, this.productId)
    }

    private fun decreaseStock(amount: Int): Inventory {
        validateStock(amount)
        return Inventory(this.id, this.quantity, this.stock - amount, this.version, this.productId)
    }

    private fun validateStock(amount: Int) {
        require(this.stock - amount >= 0) { "재고는 음수가 될 수 없습니다." }
    }

    companion object {
        fun create(quantity: Int, productId: Long): Inventory {
            require(quantity >= 0) { "수량은 0 이상이어야 합니다" }
            return Inventory(
                quantity = quantity,
                stock = quantity,
                productId = productId
            )
        }
    }

}