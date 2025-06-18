package com.eatngo.order.domain

import com.eatngo.customer.domain.Customer
import com.eatngo.store.domain.Store
import java.time.LocalDateTime

class OrderStatusHistory(
    val id: Long = 0,
    val status: Status,
    val updatedAt: LocalDateTime,
    val updatedBy: Long,
    val userType: String,
) {
    companion object {
        fun from(
            status: Status,
            customer: Customer,
        ) = OrderStatusHistory(
            status = status,
            updatedAt = LocalDateTime.now(),
            updatedBy = customer.id,
            userType = "CUSTOMER",
        )

        fun from(
            status: Status,
            customerId: Long,
        ) = OrderStatusHistory(
            status = status,
            updatedAt = LocalDateTime.now(),
            updatedBy = customerId,
            userType = "CUSTOMER",
        )

        fun from(
            status: Status,
            store: Store,
        ) = OrderStatusHistory(
            status = status,
            updatedAt = LocalDateTime.now(),
            updatedBy = store.id,
            userType = "STORE",
        )
    }
}
