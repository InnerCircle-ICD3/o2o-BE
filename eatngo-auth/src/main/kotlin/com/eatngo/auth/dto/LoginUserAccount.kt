package com.eatngo.auth.dto

import com.eatngo.user_account.oauth2.constants.Role

class LoginUserAccount(
    override val userAccountId: Long,
    override val nickname: String? = null,
    override val roles: List<String>
) : LoginUser {
    override fun getCurrentRole(): Role = Role.USER
}