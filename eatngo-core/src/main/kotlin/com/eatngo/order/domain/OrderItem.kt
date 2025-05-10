package com.eatngo.order.domain

class OrderItem (
    val id: Long,
    val productId: Long,
    val name: String,
    val quantity: Int,
    val price: Int,
){

}