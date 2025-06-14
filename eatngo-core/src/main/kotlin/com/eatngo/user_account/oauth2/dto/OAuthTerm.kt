package com.eatngo.user_account.oauth2.dto

import java.time.LocalDateTime

data class OAuthTerm(
    val tag: String,
    val agreedAt: LocalDateTime,
)
