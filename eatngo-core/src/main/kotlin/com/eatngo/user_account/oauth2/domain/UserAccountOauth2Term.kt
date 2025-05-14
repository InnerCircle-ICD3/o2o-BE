package com.eatngo.user_account.oauth2.domain

import java.time.ZonedDateTime

class UserAccountOauth2Term(
    val id: Long = 0,
    val userAccountOauth2: UserAccountOauth2,
    val tag: String,
    val agreedAt: ZonedDateTime,
    val createdAt: ZonedDateTime,
    val isDeleted: Boolean = false,
    val deletedAt: ZonedDateTime? = null,
) {
    companion object {
        fun of(
            userAccountOauth2: UserAccountOauth2, tag: String
        ): UserAccountOauth2Term {
            return UserAccountOauth2Term(
                userAccountOauth2 = userAccountOauth2,
                tag = tag,
                agreedAt = ZonedDateTime.now(),
                createdAt = ZonedDateTime.now(
                )
            )
        }
    }
}