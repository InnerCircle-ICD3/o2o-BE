package com.eatngo.auth.dto

class LoginUserAccount(
    override val userAccountId: Long,
    override val roles: List<String>
) : LoginUser