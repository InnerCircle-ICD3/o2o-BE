package com.eatngo.order.domain

import com.eatngo.customer.domain.Customer
import com.eatngo.store_owner.domain.StoreOwner
import java.time.LocalDateTime

class Order(
    val id: Long = 0,
    val orderNumber: Long,
    val orderItems: List<OrderItem>,
    val customerId: Long,
    val storeId: Long,
    var status: Status,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,
) {
    private fun isEditable(customer: Customer){
        require(customer.id == this.customerId) { "Customer can't edit this order" }
    }

    private fun isEditable(store: StoreOwner){
        require(store.id == this.storeId) { "Store can't be edited this order" }
    }

    fun toCancel(customer: Customer){
        isEditable(customer)
        status = status.cancel()
        updatedAt = LocalDateTime.now()
    }

    fun toCancel(store: StoreOwner){
        isEditable(store)
        status = status.cancel()
        updatedAt = LocalDateTime.now()
    }

    fun toConfirm(store: StoreOwner){
        isEditable(store)
        status = status.confirm()
        updatedAt = LocalDateTime.now()
    }

    fun toDone(customer: Customer){
        isEditable(customer)
        status = status.done()
        updatedAt = LocalDateTime.now()
    }

    companion object {
        fun create(customerId: Long, storeId: Long, orderNumber: Long, orderItems: List<OrderItem>): Order {
            return Order(
                0,
                orderNumber,
                orderItems,
                customerId,
                storeId,
                Status.CREATED,
                LocalDateTime.now(),
                LocalDateTime.now()
            )
        }
    }
}