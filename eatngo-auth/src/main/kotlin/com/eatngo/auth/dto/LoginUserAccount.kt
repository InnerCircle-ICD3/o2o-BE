package com.eatngo.auth.dto

class LoginUserAccount(
    override val userAccountId: Long,
    override val nickname: String? = null,
    override val roles: List<String>
) : LoginUser