package com.eatngo.user_account.rdb.entity

import com.eatngo.common.BaseJpaEntity
import com.eatngo.constants.DELETED_FILTER
import com.eatngo.user_account.domain.UserAccount
import com.eatngo.user_account.vo.EmailAddress
import jakarta.persistence.*
import org.hibernate.annotations.Filter

@Filter(name = DELETED_FILTER)
@Entity
class UserAccountJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = true, length = 255)
    val email: String?,

    @OneToMany(mappedBy = "userAccount", fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @Filter(name = DELETED_FILTER)
    val oAuth2: MutableList<UserAccountOAuth2JpaEntity> = mutableListOf(),

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @Filter(name = DELETED_FILTER)
    val roles: MutableList<UserAccountRoleJpaEntity> = mutableListOf(),
) : BaseJpaEntity() {

    companion object {
        fun from(account: UserAccount) = UserAccountJpaEntity(
            id = account.id,
            email = account.email?.value,
        ).also {
            account.oAuth2.forEach { oAuth2 ->
                // addLast
                it.oAuth2.add(UserAccountOAuth2JpaEntity.of(oAuth2, it))
            }
            account.roles.forEach { role ->
                // addLast
                it.roles.add(UserAccountRoleJpaEntity.of(role, it))
            }
        }

        fun toUserAccount(account: UserAccountJpaEntity) = with(account) {
            UserAccount(
                id = id,
                email = email?.let { EmailAddress(it) },
                createdAt = createdAt,
                updatedAt = updatedAt,
                deletedAt = deletedAt,
                roles = roles.map { UserAccountRoleJpaEntity.toRole(it) },
            )
        }
    }
}