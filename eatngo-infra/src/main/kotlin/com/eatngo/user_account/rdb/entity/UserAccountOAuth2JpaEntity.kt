package com.eatngo.user_account.rdb.entity

import com.eatngo.common.BaseJpaEntity
import com.eatngo.constants.DELETED_FILTER
import com.eatngo.user_account.domain.UserAccount
import com.eatngo.user_account.oauth2.constants.Oauth2Provider
import com.eatngo.user_account.oauth2.domain.UserAccountOAuth2
import com.eatngo.user_account.vo.EmailAddress
import com.eatngo.user_account.vo.Nickname
import jakarta.persistence.*
import org.hibernate.annotations.Filter
import java.time.LocalDateTime

@Filter(name = DELETED_FILTER)
@Entity
@Table(name = "user_account_oauth2")
class UserAccountOAuth2JpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Filter(name = DELETED_FILTER)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", nullable = false)
    val userAccount: UserAccountJpaEntity,

    @Column(nullable = true, length = 255)
    val email: String? = null,

    @Column(length = 100)
    val nickname: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val provider: Oauth2Provider,

    @Column(nullable = false, length = 255)
    val userKey: String,

    @Column(columnDefinition = "TEXT")
    val accessToken: String? = null,

    val expireAt: LocalDateTime? = null,

    @Column(length = 500)
    val scopes: String? = null,

    @OneToMany(mappedBy = "userAccountOAuth2", fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @Filter(name = DELETED_FILTER)
    val terms: MutableList<UserAccountOAuth2TermJpaEntity> = mutableListOf(),
) : BaseJpaEntity() {
    companion object {
        fun of(
            userAccountOauth2: UserAccountOAuth2,
            accountJpaEntity: UserAccountJpaEntity
        ) = UserAccountOAuth2JpaEntity(
            id = userAccountOauth2.id,
            userAccount = accountJpaEntity,
            email = userAccountOauth2.email.let { it?.toString() },
            nickname = userAccountOauth2.nickname,
            provider = userAccountOauth2.provider,
            userKey = userAccountOauth2.userKey,
            accessToken = userAccountOauth2.accessToken,
            expireAt = userAccountOauth2.expireAt,
            scopes = userAccountOauth2.scopes,
        ).also {
            userAccountOauth2.terms.forEach { term ->
                // addLast
                it.terms.add(UserAccountOAuth2TermJpaEntity.of(term, it))
            }
        }

        fun toUserAccount(accountOauth2: UserAccountOAuth2JpaEntity): UserAccount = with(accountOauth2) {
            val emailAddress = email?.let { EmailAddress(it) }
            val userAccount = UserAccount(
                id = userAccount.id,
                email = emailAddress,
                nickname = nickname?.let { Nickname(it) },
                createdAt = createdAt,
                updatedAt = updatedAt,
                deletedAt = deletedAt,
            )

            userAccount.addOauth2(
                UserAccountOAuth2(
                    id = id,
                    userAccount = userAccount,
                    email = emailAddress,
                    nickname = nickname,
                    provider = provider,
                    userKey = userKey,
                    accessToken = accessToken,
                    expireAt = expireAt,
                    scopes = scopes
                )
            )
            userAccount
        }
    }

    override fun delete() {
        super.delete()
        terms.forEach { it.delete() }
    }
}