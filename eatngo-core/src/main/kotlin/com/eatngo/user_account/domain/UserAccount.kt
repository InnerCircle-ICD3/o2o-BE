package com.eatngo.user_account.domain

import com.eatngo.user_account.dto.UserAccountUpdateDto
import com.eatngo.user_account.oauth2.constants.Role
import com.eatngo.user_account.oauth2.domain.UserAccountOauth2
import com.eatngo.user_account.oauth2.dto.Oauth2
import com.eatngo.user_account.vo.EmailAddress
import java.time.LocalDateTime

class UserAccount(
    val id: Long = 0,
    val email: EmailAddress?,
    var nickname: String? = null,
    var roles: List<Role> = mutableListOf(),
    val createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null,
    var deletedAt: LocalDateTime? = null,
) {
    private val _oauth2 = mutableListOf<UserAccountOauth2>()
    val oauth2: List<UserAccountOauth2> get() = _oauth2

    fun addOauth2(oauth2: UserAccountOauth2) {
        _oauth2.add(oauth2)
    }

    fun update(userAccountUpdateDto: UserAccountUpdateDto) {
        userAccountUpdateDto.nickname?.let {
            this.nickname = it
        }
    }

    companion object {

        fun create(oauth2: Oauth2): UserAccount {
            val userAccount = UserAccount(
                email = oauth2.email.let { it?.let { EmailAddress(it) } },
                nickname = oauth2.nickname,
            )
            userAccount.addOauth2(
                UserAccountOauth2.of(
                    account = userAccount,
                    oauth2 = oauth2
                )
            )
            userAccount.roles = listOf(Role.USER)

            return userAccount
        }

    }

}