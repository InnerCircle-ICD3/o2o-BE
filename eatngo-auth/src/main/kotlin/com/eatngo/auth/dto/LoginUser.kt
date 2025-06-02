package com.eatngo.auth.dto

import com.eatngo.user_account.oauth2.constants.Role

interface LoginUser {
    fun getCurrentRole(): Role

    val userAccountId: Long
    val nickname: String?
    val roles: List<String>
}