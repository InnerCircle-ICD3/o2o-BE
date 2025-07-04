package com.eatngo.order.rdb.entity

import com.eatngo.common.BaseJpaEntity
import com.eatngo.constants.DELETED_FILTER
import com.eatngo.order.domain.OrderItem
import jakarta.persistence.*
import org.hibernate.annotations.Filter


@Entity
@Filter(name = DELETED_FILTER)
@Table(name = "orderItem")
class OrderItemJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val originPrice: Int,
    val finalPrice: Int,
    val imageUrl: String?,
    @ManyToOne
    @JoinColumn(name = "order_id")
    val order: OrderJpaEntity
) : BaseJpaEntity() {
    companion object {
        fun from(orderItem: OrderItem, order: OrderJpaEntity): OrderItemJpaEntity {
            return OrderItemJpaEntity(
                id = orderItem.id,
                productId = orderItem.productId,
                productName = orderItem.productName,
                quantity = orderItem.quantity,
                originPrice = orderItem.originPrice,
                finalPrice = orderItem.finalPrice,
                imageUrl = orderItem.imageUrl,
                order = order
            )
        }

        fun toOrderItem(orderItemJpaEntity: OrderItemJpaEntity) =
            with(orderItemJpaEntity) {
                OrderItem(
                    id = id,
                    productId = productId,
                    productName = productName,
                    quantity = quantity,
                    originPrice = originPrice,
                    finalPrice = finalPrice,
                    imageUrl = imageUrl,
                )
            }
    }
}