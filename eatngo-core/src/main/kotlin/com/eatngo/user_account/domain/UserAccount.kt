package com.eatngo.user_account.domain

import com.eatngo.user_account.dto.UserAccountUpdateDto
import com.eatngo.user_account.oauth2.constants.Role
import com.eatngo.user_account.oauth2.domain.UserAccountOAuth2
import com.eatngo.user_account.oauth2.dto.OAuth2
import com.eatngo.user_account.vo.EmailAddress
import com.eatngo.user_account.vo.Nickname
import java.time.LocalDateTime

class UserAccount(
    val id: Long = 0,
    val email: EmailAddress?,
    var nickname: Nickname? = null,
    var roles: MutableSet<UserRole> = mutableSetOf(),
    val createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null,
    var deletedAt: LocalDateTime? = null,
) {
    private val _oAuth2 = mutableListOf<UserAccountOAuth2>()
    val oAuth2: List<UserAccountOAuth2> get() = _oAuth2

    fun addOauth2(oAuth2: UserAccountOAuth2) {
        _oAuth2.add(oAuth2)
    }

    fun update(userAccountUpdateDto: UserAccountUpdateDto) {
        userAccountUpdateDto.nickname?.let { this.nickname = it }
    }

    fun updateOauth2(userAccountOauth2: UserAccountOAuth2) {
        oAuth2.find { it.provider == userAccountOauth2.provider && it.userKey == userAccountOauth2.userKey }
            ?.update(userAccountOauth2)
    }

    companion object {

        fun create(oAuth2: OAuth2): UserAccount {
            val userAccount = UserAccount(
                email = oAuth2.email.let { it?.let { EmailAddress(it) } },
                nickname = oAuth2.nickname?.let { Nickname(it) },
            )
            userAccount.addOauth2(
                UserAccountOAuth2.of(
                    account = userAccount,
                    oAuth2 = oAuth2
                )
            )
            userAccount.roles = mutableSetOf(UserRole(null, Role.USER))

            return userAccount
        }

    }

}