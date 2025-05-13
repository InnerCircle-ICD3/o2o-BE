package com.eatngo.auth.dto

data class LoginCustomer(
    override val userAccountId: Long,
    override val roles: List<String>,
    val customerId: Long
) : LoginUser