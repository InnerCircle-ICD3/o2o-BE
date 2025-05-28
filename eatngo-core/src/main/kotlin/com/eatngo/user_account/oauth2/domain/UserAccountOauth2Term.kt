package com.eatngo.user_account.oauth2.domain

import java.time.LocalDateTime

class UserAccountOauth2Term(
    val id: Long = 0,
    val userAccountOauth2: UserAccountOAuth2,
    val tag: String,
    val agreedAt: LocalDateTime,
    val createdAt: LocalDateTime? = null,
    val deletedAt: LocalDateTime? = null,
) {
    companion object {
        fun of(
            userAccountOauth2: UserAccountOAuth2, tag: String
        ): UserAccountOauth2Term {
            return UserAccountOauth2Term(
                userAccountOauth2 = userAccountOauth2,
                tag = tag,
                agreedAt = userAccountOauth2.createdAt ?: LocalDateTime.now(),
            )
        }
    }
}