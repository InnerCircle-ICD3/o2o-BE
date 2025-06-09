package com.eatngo.order.domain

class OrderItem(
    val id: Long = 0,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val originPrice: Int,
    val finalPrice: Int,
    val imageUrl: String?
) {
    companion object {
        fun of(
            productId: Long,
            name: String,
            quantity: Int,
            originPrice: Int,
            finalPrice: Int,
            imageUrl: String?,
            id: Long = 0,
        ): OrderItem {
            return OrderItem(
                id = id,
                productId = productId,
                productName = name,
                quantity = quantity,
                originPrice = originPrice,
                finalPrice = finalPrice,
                imageUrl = imageUrl
            )
        }
    }
}