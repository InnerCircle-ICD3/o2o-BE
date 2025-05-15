package com.eatngo.product.domain

class Inventory(
    val quantity: Int,
    val stock: Int,
) {
    companion object {
        fun create(quantity: Int): Inventory {
            return Inventory(quantity = quantity, stock = quantity)
        }
    }
}