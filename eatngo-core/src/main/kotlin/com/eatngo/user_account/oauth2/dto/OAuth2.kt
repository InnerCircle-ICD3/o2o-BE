package com.eatngo.user_account.oauth2.dto

import com.eatngo.user_account.oauth2.constants.OAuth2Provider
import java.time.LocalDateTime

interface OAuth2 {

    val id: Long
    val email: String?
    val provider: OAuth2Provider
    val terms: List<OAuthTerm>
    val principal: String
    val nickname: String?
    val token: String
    val expiresAt: LocalDateTime?
    val scopes: String
}