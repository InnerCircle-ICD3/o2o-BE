package com.eatngo.user_account.rdb.entity

import com.eatngo.common.BaseJpaEntity
import com.eatngo.constants.DELETED_FILTER
import com.eatngo.user_account.oauth2.constants.Role
import jakarta.persistence.*
import org.hibernate.annotations.Filter

@Filter(name = DELETED_FILTER)
@Entity
@Table(name = "user_account_role")
class UserAccountRoleJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    val account: UserAccountJpaEntity,

    @Enumerated(EnumType.STRING)
    val role: Role,
) : BaseJpaEntity() {


    companion object {
        fun of(
            role: Role = Role.USER,
            account: UserAccountJpaEntity
        ): UserAccountRoleJpaEntity {
            return UserAccountRoleJpaEntity(
                account = account,
                role = role
            )
        }

        fun toRole(accountRole: UserAccountRoleJpaEntity) = with(accountRole) {
            Role.valueOf(role.name)
        }
    }
}