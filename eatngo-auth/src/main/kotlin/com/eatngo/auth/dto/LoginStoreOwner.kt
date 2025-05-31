package com.eatngo.auth.dto

import com.eatngo.user_account.oauth2.constants.Role

data class LoginStoreOwner(
    override val userAccountId: Long,
    override val roles: List<String>,
    override val nickname: String?,
    val storeOwnerId: Long
) : LoginUser {
    override fun getCurrentRole(): Role = Role.STORE_OWNER
}