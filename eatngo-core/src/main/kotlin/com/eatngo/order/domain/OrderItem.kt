package com.eatngo.order.domain

class OrderItem(
    val id: Long = 0,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val price: Int,
) {
    companion object {
        fun of(productId: Long, name: String, quantity: Int, price: Int, id: Long = 0): OrderItem {
            return OrderItem(
                id = id,
                productId = productId,
                productName = name,
                quantity = quantity,
                price = price,
            )
        }
    }
}