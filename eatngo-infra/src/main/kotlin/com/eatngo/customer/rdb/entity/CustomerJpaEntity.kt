package com.eatngo.customer.rdb.entity

import com.eatngo.common.BaseJpaEntity
import com.eatngo.constants.DELETED_FILTER
import com.eatngo.customer.domain.Customer
import com.eatngo.user_account.rdb.entity.UserAccountJpaEntity
import jakarta.persistence.*
import org.hibernate.annotations.Filter

@Filter(name = DELETED_FILTER)
@Entity
class CustomerJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @OneToOne(fetch = FetchType.LAZY)
    val account: UserAccountJpaEntity,
) : BaseJpaEntity() {
    companion object {
        fun from(customer: Customer): CustomerJpaEntity {
            return CustomerJpaEntity(
                id = customer.id,
                account = UserAccountJpaEntity.from(customer.account),
            )
        }

        fun toCustomer(customerJpaEntity: CustomerJpaEntity): Customer {
            return Customer(
                id = customerJpaEntity.id,
                account = UserAccountJpaEntity.toUserAccount(customerJpaEntity.account),
                createdAt = customerJpaEntity.createdAt,
                updatedAt = customerJpaEntity.updatedAt,
                deletedAt = customerJpaEntity.deletedAt,
            )
        }
    }

}