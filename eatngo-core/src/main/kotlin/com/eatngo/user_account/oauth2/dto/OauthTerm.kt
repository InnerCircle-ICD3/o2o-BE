package com.eatngo.user_account.oauth2.dto

import java.time.LocalDateTime

data class OauthTerm(
    val tag: String,
    val agreedAt: LocalDateTime,
)
