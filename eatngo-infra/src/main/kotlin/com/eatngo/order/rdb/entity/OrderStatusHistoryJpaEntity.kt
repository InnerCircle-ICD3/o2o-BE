package com.eatngo.order.rdb.entity

import com.eatngo.order.domain.OrderStatusHistory
import com.eatngo.order.domain.Status
import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
@Table(name = "order_status_histories")
class OrderStatusHistoryJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Enumerated(EnumType.STRING)
    val status: Status,
    val updatedAt: LocalDateTime,
    val updatedBy: Long,
    val userType: String,
    @ManyToOne
    @JoinColumn(name = "order_id")
    val order: OrderJpaEntity
) {
    companion object {
        fun from(orderStatusHistory: OrderStatusHistory, orderJpaEntity: OrderJpaEntity) = with(orderStatusHistory) {
            OrderStatusHistoryJpaEntity(
                id = id,
                status = status,
                updatedAt = updatedAt,
                updatedBy = updatedBy,
                userType = userType,
                order = orderJpaEntity
            )
        }

        fun toDomain(orderStatusHistoryJpaEntity: OrderStatusHistoryJpaEntity) = with(orderStatusHistoryJpaEntity){
            OrderStatusHistory(
                id = id,
                status = status,
                updatedAt = updatedAt,
                updatedBy = updatedBy,
                userType = userType,
            )
        }
    }
}