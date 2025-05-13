package com.eatngo.user_account.oauth2.dto

import com.eatngo.user_account.oauth2.constants.Oauth2Provider

interface Oauth2 {

    fun getTerms(): List<OauthTerm>
    fun getEmail(): String

    fun getNickname(): String?
    fun getProvider(): Oauth2Provider

    fun getPrincipal(): String
}