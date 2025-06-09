package com.eatngo.user_account.domain

import com.eatngo.user_account.oauth2.constants.Role

class UserRole(
    val id: Long? = 0,
    val role: Role
)