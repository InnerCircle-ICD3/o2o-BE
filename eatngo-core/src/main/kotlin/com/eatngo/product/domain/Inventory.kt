package com.eatngo.product.domain

class Inventory(
    val quantity: Int, // 점주가 지정한 상품의 수량
    val stock: Int, // 현재 재고량
) {
    fun increaseStock(amount: Int): Inventory {
        return Inventory(this.stock + amount, this.stock)
    }

    fun decreaseStock(amount: Int): Inventory {
        validateStock(amount)
        return Inventory(this.stock - amount, this.stock)
    }

    private fun validateStock(amount: Int) {
        if (this.stock - amount < 0) {
            throw IllegalArgumentException("재고는 음수가 될 수 없습니다.")
        }
    }

    companion object {
        fun create(quantity: Int): Inventory {
            require(quantity >= 0) { "수량은 0 이상이어야 합니다" }
            return Inventory(quantity = quantity, stock = quantity)
        }
    }
}