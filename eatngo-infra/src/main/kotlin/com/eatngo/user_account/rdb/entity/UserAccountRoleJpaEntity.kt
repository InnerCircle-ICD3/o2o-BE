package com.eatngo.user_account.rdb.entity

import com.eatngo.common.BaseJpaEntity
import com.eatngo.constants.DELETED_FILTER
import com.eatngo.user_account.domain.UserRole
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
            roleId: Long? = 0L,
            role: Role = Role.USER,
            account: UserAccountJpaEntity
        ): UserAccountRoleJpaEntity {
            return UserAccountRoleJpaEntity(
                id = roleId ?: 0L,
                account = account,
                role = role
            )
        }

        fun toRole(accountRole: UserAccountRoleJpaEntity) = with(accountRole) {
            UserRole(
                id = id,
                role = Role.valueOf(role.name)
            )
        }
    }
}