package com.eatngo.user_account.domain

import com.eatngo.user_account.oauth2.domain.UserAccountOauth2
import com.eatngo.user_account.oauth2.dto.Oauth2
import com.eatngo.user_account.vo.EmailAddress
import java.time.ZonedDateTime

class UserAccount(
    val id: Long = 0,
    val email: EmailAddress,
    val createdAt: ZonedDateTime,
    var updatedAt: ZonedDateTime,
    var isDeleted: Boolean = false,
    var deletedAt: ZonedDateTime? = null,
) {
    private val _oauth2 = mutableListOf<UserAccountOauth2>()
    val oauth2: List<UserAccountOauth2> get() = _oauth2

    fun addOauth2(oauth2: UserAccountOauth2) {
        _oauth2.add(oauth2)
    }

    companion object {

        fun create(oauth2: Oauth2): UserAccount {
            val userAccount = UserAccount(
                email = EmailAddress.from(oauth2.getEmail()),
                createdAt = ZonedDateTime.now(),
                updatedAt = ZonedDateTime.now()
            )
            userAccount.addOauth2(
                UserAccountOauth2.of(
                    account = userAccount,
                    oauth2 = oauth2
                )
            )

            return userAccount
        }
    }

    fun delete() {
        if (isDeleted) {
            throw IllegalStateException("Store owner is already deleted")
        }
        isDeleted = true
        deletedAt = ZonedDateTime.now()
    }
}