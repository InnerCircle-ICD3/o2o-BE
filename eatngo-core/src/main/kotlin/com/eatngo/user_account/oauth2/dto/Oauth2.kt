package com.eatngo.user_account.oauth2.dto

import com.eatngo.user_account.oauth2.constants.Oauth2Provider

interface Oauth2 {

    val id: Long
    val email: String?
    val provider: Oauth2Provider
    val terms: List<OauthTerm>
    val principal: String
    val nickname: String?
}