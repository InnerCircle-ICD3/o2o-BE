package com.eatngo.user_account.rdb.entity

import com.eatngo.common.SoftDeletableJpaEntity
import com.eatngo.constants.DELETED_FILTER
import com.eatngo.user_account.oauth2.domain.UserAccountOauth2Term
import jakarta.persistence.*
import org.hibernate.annotations.Filter
import java.time.LocalDateTime

@Filter(name = DELETED_FILTER)
@Entity
class UserAccountOauth2TermJpaEntity(
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
        fun from(userAccountOauth2Term: UserAccountOauth2Term) =
            UserAccountOauth2TermJpaEntity(
                userAccountOAuth2 = UserAccountOAuth2JpaEntity.from(userAccountOauth2Term.userAccountOauth2),
                tag = userAccountOauth2Term.tag,
                agreedAt = userAccountOauth2Term.agreedAt,
            )
    }
}