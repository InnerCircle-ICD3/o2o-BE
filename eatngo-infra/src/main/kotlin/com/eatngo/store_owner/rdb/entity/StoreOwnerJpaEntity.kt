package com.eatngo.store_owner.rdb.entity

import com.eatngo.common.BaseJpaEntity
import com.eatngo.constants.DELETED_FILTER
import com.eatngo.store_owner.domain.StoreOwner
import com.eatngo.user_account.rdb.entity.UserAccountJpaEntity
import jakarta.persistence.*
import org.hibernate.annotations.Filter

@Filter(name = DELETED_FILTER)
@Entity
@Table(name = "store_owner")
class StoreOwnerJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @OneToOne(fetch = FetchType.LAZY)
    val account: UserAccountJpaEntity,
) : BaseJpaEntity() {
    companion object {
        fun from(storeOwner: StoreOwner): StoreOwnerJpaEntity {
            return StoreOwnerJpaEntity(
                id = storeOwner.id,
                account = UserAccountJpaEntity.from(storeOwner.account),
            )
        }

        fun toStoreOwner(storeOwnerJpaEntity: StoreOwnerJpaEntity) = StoreOwner(
            id = storeOwnerJpaEntity.id,
            account = UserAccountJpaEntity.toUserAccount(storeOwnerJpaEntity.account),
            createdAt = storeOwnerJpaEntity.createdAt,
            updatedAt = storeOwnerJpaEntity.updatedAt,
            deletedAt = storeOwnerJpaEntity.deletedAt,
        )
    }
}