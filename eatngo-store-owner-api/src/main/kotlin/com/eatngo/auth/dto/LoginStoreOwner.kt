package com.eatngo.auth.dto

import com.eatngo.auth.dto.LoginUser

data class LoginStoreOwner(
    override val userAccountId: Long,
    override val roles: List<String>,
    val storeOwnerId: Long
) : LoginUser