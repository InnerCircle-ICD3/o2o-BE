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

    @Column(nullable = true, length = 255)
    val nickname: String?,

    @OneToMany(mappedBy = "userAccount", fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @Filter(name = DELETED_FILTER)
    val oauth2: MutableList<UserAccountOAuth2JpaEntity> = mutableListOf(),

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @Filter(name = DELETED_FILTER)
    val roles: MutableList<UserAccountRoleJpaEntity> = mutableListOf(),
) : BaseJpaEntity() {

    companion object {
        fun from(account: UserAccount) = UserAccountJpaEntity(
            id = account.id,
            email = account.email?.value,
            nickname = account.nickname,
        ).also {
            account.oauth2.forEach { oauth2 ->
                it.oauth2.add(UserAccountOAuth2JpaEntity.of(oauth2, it))
            }
            account.roles.forEach { role ->
                it.roles.add(UserAccountRoleJpaEntity.of(role, it))
            }
        }

        fun toUserAccount(account: UserAccountJpaEntity) = with(account) {
            UserAccount(
                id = id,
                email = email?.let { EmailAddress(it) },
                nickname = account.nickname,
                createdAt = createdAt,
                updatedAt = updatedAt,
                deletedAt = deletedAt,
                roles = roles.map { UserAccountRoleJpaEntity.toRole(it) },
            )
        }
    }

    override fun delete() {
        super.delete()
        oauth2.forEach { it.delete() }
        roles.forEach { it.delete() }
    }
}