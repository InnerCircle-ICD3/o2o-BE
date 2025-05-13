package com.eatngo.user_account.oauth2.dto

import com.eatngo.user_account.oauth2.constants.Oauth2Provider

data class KakaoOauth2(
    val id: Long,
    val email: String,
    val term: List<OauthTerm> = emptyList(),
    val principal: String,
    val nickname: String?,
) : Oauth2 {
    override fun getTerms(): List<OauthTerm> {
        return term
    }

    override fun getEmail(): String {
        return email
    }

    override fun getNickname(): String? {
        return nickname
    }


    override fun getProvider(): Oauth2Provider {
        return Oauth2Provider.KAKAO
    }

    override fun getPrincipal(): String {
        return principal
    }

}
