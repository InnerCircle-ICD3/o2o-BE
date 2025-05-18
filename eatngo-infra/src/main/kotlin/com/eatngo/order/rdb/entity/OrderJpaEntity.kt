package com.eatngo.order.rdb.entity

import com.eatngo.common.BaseJpaEntity
import com.eatngo.order.domain.Order
import com.eatngo.order.domain.OrderItem
import com.eatngo.order.domain.Status
import jakarta.persistence.*

@Entity
class OrderJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val orderNumber: Long,
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    val orderItems: MutableList<OrderItemJpaEntity> = mutableListOf(),
    val customerId: Long,
    val storeId: Long,
    @Enumerated(EnumType.STRING)
    val status: Status
) : BaseJpaEntity() {

    companion object {
        fun from(order: Order): OrderJpaEntity {
            val orderJpaEntity = OrderJpaEntity(
                id = order.id,
                orderNumber = order.orderNumber,
                customerId = order.customerId,
                storeId = order.storeId,
                status = order.status
            )

            order.orderItems.forEach { orderItem: OrderItem ->
                orderJpaEntity.orderItems.add(
                    OrderItemJpaEntity.from(orderItem, orderJpaEntity)
                )
            }

            return orderJpaEntity
        }

        fun toOrder(orderJpaEntity: OrderJpaEntity) = with(orderJpaEntity) {
            Order(
                id = id,
                orderNumber = orderNumber,
                orderItems = orderItems.map(OrderItemJpaEntity::toOrderItem),
                customerId = customerId,
                storeId = storeId,
                status = status,
                createdAt = createdAt,
                updatedAt = updatedAt,
            )
        }
    }
}