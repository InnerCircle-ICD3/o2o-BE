package com.eatngo.oauth2

import com.eatngo.oauth2.client.KakaoOAuth2Client
import com.eatngo.user_account.oauth2.constants.OAuth2Provider
import org.springframework.stereotype.Component

@Component
class KakaoOAuth2Service(
    private val kakaoOAuth2Client: KakaoOAuth2Client,
) : OAuth2Service {
    override fun supports(provider: OAuth2Provider) = provider == OAuth2Provider.KAKAO

    override fun unlink(accessToken: String) {
        kakaoOAuth2Client.unlink("Bearer $accessToken")
    }
}