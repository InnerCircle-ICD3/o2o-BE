package com.eatngo.auth.handler

import com.eatngo.auth.token.TokenProvider
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2LoginSuccessHandler(
    private val tokenProvider: TokenProvider
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        if (authentication.principal !is OAuth2User) {
            throw IllegalArgumentException("인증 주체가 OAuth2User 타입이 아닙니다")
        }
        val oAuth2User = authentication.principal as OAuth2User
        val userId = extractUserId(oAuth2User)

        val accessToken = tokenProvider.createAccessToken(userId)
        val refreshToken = tokenProvider.createRefreshToken(userId)

        val accessCookie = buildCookie("access_token", accessToken, maxAge = 1000 * 60 * 10 * 6)   // 1 hour
        val refreshCookie = buildCookie("refresh_token", refreshToken, maxAge = 1000 * 60 * 60 * 24 * 14) // 14 days
        response.addCookie(accessCookie)
        response.addCookie(refreshCookie)
        response.addHeader(
            "Set-Cookie",
            "${accessCookie.name}=${accessCookie.value}; Max-Age=${accessCookie.maxAge}; Path=${accessCookie.path}; HttpOnly; Secure; SameSite=Lax"
        )
        response.addHeader(
            "Set-Cookie",
            "${refreshCookie.name}=${refreshCookie.value}; Max-Age=${refreshCookie.maxAge}; Path=${refreshCookie.path}; HttpOnly; Secure; SameSite=Lax"
        )
        response.status = HttpStatus.OK.value()
        response.contentType = "application/json;charset=UTF-8"
        response.writer.write(
            """
                {
                    "success": "true",
                    "data": {
                        "userId": "$userId"
                    }
                }
            """.trimIndent()
        ) // TODO 응답 포맷 변경
    }

    private fun extractUserId(oAuth2User: OAuth2User): String {
        val provider = oAuth2User.attributes["provider"] as? String ?: "unknown"

        return when (provider) {
            "kakao" -> oAuth2User.attributes["id"]?.toString()
            "google" -> oAuth2User.getAttribute<String>("sub")
            "naver" -> oAuth2User.attributes["id"]?.toString()
            else -> oAuth2User.getAttribute<String>("sub")
        } ?: "unknown"
    }

    private fun buildCookie(name: String, value: String, maxAge: Int): Cookie {
        return Cookie(name, value).apply {
            isHttpOnly = true
            secure = true
            path = "/"
            this.maxAge = maxAge
        }
    }
}