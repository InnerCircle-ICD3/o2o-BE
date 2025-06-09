package com.eatngo.user_account.oauth2.dto

import com.eatngo.user_account.oauth2.constants.Oauth2Provider
import java.time.LocalDateTime


data class KakaoOAuth2(
    private val attributes: Map<String, Any>,
    override val provider: Oauth2Provider,
    override val token: String,
    override val expiresAt: LocalDateTime? = null,
    override val scopes: String,
) : OAuth2 {

    override val id: Long = (attributes["id"] as? Number)?.toLong()
        ?: throw IllegalStateException("Kakao OAuth2 response does not contain 'id'")
    private val kakaoAccount = attributes["kakao_account"] as? Map<*, *> ?: emptyMap<Any, Any>()
    private val profile = kakaoAccount["profile"] as? Map<*, *> ?: emptyMap<Any, Any>()
    override val email: String? = kakaoAccount["email"] as? String
    override val nickname: String? = profile["nickname"] as? String
    override val principal: String = id.toString()
    override val terms: List<OauthTerm> = emptyList()
}
