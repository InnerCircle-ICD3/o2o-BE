package com.eatngo.auth.dto

data class LoginStoreOwner(
    override val userAccountId: Long,
    override val roles: List<String>,
    override val nickname: String?,
    val storeOwnerId: Long
) : LoginUser