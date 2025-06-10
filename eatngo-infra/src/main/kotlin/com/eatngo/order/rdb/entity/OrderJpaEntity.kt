package com.eatngo.order.rdb.entity

import com.eatngo.common.BaseJpaEntity
import com.eatngo.constants.DELETED_FILTER
import com.eatngo.order.domain.Order
import com.eatngo.order.domain.OrderItem
import com.eatngo.order.domain.Status
import jakarta.persistence.*
import org.hibernate.annotations.Filter
import java.time.LocalDateTime

@Filter(name = DELETED_FILTER)
@Table(name = "orders")
@Entity
class OrderJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val orderNumber: Long,
    val pickupDateTime: LocalDateTime,
    @Filter(name = DELETED_FILTER)
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    val orderItems: MutableList<OrderItemJpaEntity> = mutableListOf(),
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    val statusChangedHistories: MutableList<OrderStatusHistoryJpaEntity> = mutableListOf(),
    val customerId: Long,
    val nickname: String,
    val storeId: Long,
    @Enumerated(EnumType.STRING)
    var status: Status
) : BaseJpaEntity() {

    fun update(order: Order) {
        status = order.status
    }

    companion object {
        fun from(order: Order): OrderJpaEntity {
            val orderJpaEntity = OrderJpaEntity(
                id = order.id,
                orderNumber = order.orderNumber,
                pickupDateTime = order.pickupDateTime,
                customerId = order.customerId,
                nickname = order.nickname,
                storeId = order.storeId,
                status = order.status,
            )

            order.orderItems.forEach { orderItem: OrderItem ->
                orderJpaEntity.orderItems.add(
                    OrderItemJpaEntity.from(orderItem, orderJpaEntity)
                )
            }

            order.statusChangedHistories.forEach {
                orderJpaEntity.statusChangedHistories.add(
                    OrderStatusHistoryJpaEntity.from(orderStatusHistory = it, orderJpaEntity = orderJpaEntity)
                )
            }

            return orderJpaEntity
        }

        fun toOrder(orderJpaEntity: OrderJpaEntity) = with(orderJpaEntity) {
            Order(
                id = id,
                orderNumber = orderNumber,
                pickupDateTime = pickupDateTime,
                orderItems = orderItems.map(OrderItemJpaEntity::toOrderItem),
                statusChangedHistories = statusChangedHistories.map(OrderStatusHistoryJpaEntity::toDomain)
                    .toMutableList(),
                customerId = customerId,
                nickname = nickname,
                storeId = storeId,
                status = status,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}