package com.eatngo.auth.dto

import com.eatngo.user_account.oauth2.constants.Role

data class LoginCustomer(
    override val userAccountId: Long,
    override val roles: List<String>,
    override val nickname: String?,
    val customerId: Long,
    override val cookieStoreLocation: String = "/customer",
) : LoginUser {
    override fun getCurrentRole(): Role = Role.CUSTOMER


}