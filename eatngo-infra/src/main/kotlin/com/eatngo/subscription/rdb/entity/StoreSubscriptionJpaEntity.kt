package com.eatngo.subscription.rdb.entity

import com.eatngo.common.BaseJpaEntity
import com.eatngo.constants.DELETED_FILTER
import com.eatngo.subscription.domain.StoreSubscription
import jakarta.persistence.*
import org.hibernate.annotations.Filter

/**
 * 매장 구독 JPA 엔티티
 */
@Entity
@Filter(name = DELETED_FILTER)
@Table(
    name = "store_subscription",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_user_id_store_id",
            columnNames = ["user_id", "store_id"]
        )
    ]
)
class StoreSubscriptionJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "store_id", nullable = false)
    val storeId: Long
) : BaseJpaEntity() {
    companion object {
        fun from(subscription: StoreSubscription): StoreSubscriptionJpaEntity {
            return StoreSubscriptionJpaEntity(
                id = subscription.id,
                userId = subscription.userId,
                storeId = subscription.storeId
            ).apply {
                updatedAt = subscription.updatedAt
                deletedAt = subscription.deletedAt
            }
        }

        fun toSubscription(entity: StoreSubscriptionJpaEntity): StoreSubscription {
            return StoreSubscription(
                id = entity.id,
                userId = entity.userId,
                storeId = entity.storeId,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                deletedAt = entity.deletedAt
            )
        }
    }
} 