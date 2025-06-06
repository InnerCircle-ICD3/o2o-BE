package com.eatngo.user_account.rdb.entity

import com.eatngo.common.SoftDeletableJpaEntity
import com.eatngo.constants.DELETED_FILTER
import com.eatngo.user_account.oauth2.domain.UserAccountOauth2Term
import jakarta.persistence.*
import org.hibernate.annotations.Filter
import java.time.LocalDateTime

@Filter(name = DELETED_FILTER)
@Entity
@Table(name = "user_account_oauth2_term")
class UserAccountOAuth2TermJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Filter(name = DELETED_FILTER)
    @ManyToOne(fetch = FetchType.LAZY)
    val userAccountOAuth2: UserAccountOAuth2JpaEntity,

    val tag: String,

    val agreedAt: LocalDateTime,
) : SoftDeletableJpaEntity() {

    companion object {
        fun of(
            userAccountOauth2Term: UserAccountOauth2Term,
            accountOAuth2JpaEntity: UserAccountOAuth2JpaEntity,
        ) = UserAccountOAuth2TermJpaEntity(
            userAccountOAuth2 = accountOAuth2JpaEntity,
            tag = userAccountOauth2Term.tag,
            agreedAt = userAccountOauth2Term.agreedAt,
        )
    }
}