package com.eatngo.product.domain

class Inventory(
    val quantity: Int, // 점주가 지정한 상품의 수량
    val stock: Int, // 현재 재고량
) {
    companion object {
        fun create(quantity: Int): Inventory {
            require(quantity >= 0) { "수량은 0 이상이어야 합니다" }
            return Inventory(quantity = quantity, stock = quantity)
        }
    }
}