package com.eatngo.user_account.oauth2.dto

import com.eatngo.user_account.oauth2.constants.Oauth2Provider

data class KakaoOauth2(
    override val id: Long,
    override val email: String,
    override val terms: List<OauthTerm> = emptyList(),
    override val principal: String,
    override val nickname: String?,
) : Oauth2 {
    override val provider: Oauth2Provider
        get() = Oauth2Provider.KAKAO
}
