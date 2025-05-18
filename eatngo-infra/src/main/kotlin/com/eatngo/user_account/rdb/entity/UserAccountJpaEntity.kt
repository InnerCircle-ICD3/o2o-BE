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
    val email: String,

    @OneToMany(mappedBy = "userAccount", fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @Filter(name = DELETED_FILTER)
    val oauth2: List<UserAccountOAuth2JpaEntity> = emptyList(),
) : BaseJpaEntity() {
    companion object {
        fun from(account: UserAccount): UserAccountJpaEntity {
            return UserAccountJpaEntity(
                id = account.id,
                email = account.email.toString(),
            )
        }

        fun toUserAccount(account: UserAccountJpaEntity) = with(account) {
            UserAccount(
                id = id,
                email = EmailAddress.from(email),
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}