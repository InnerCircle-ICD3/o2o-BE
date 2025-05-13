package com.eatngo.auth.handler

import com.eatngo.auth.token.TokenProvider
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.web.servlet.server.Session
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
        val oAuth2User = authentication.principal as OAuth2User
        val userId = oAuth2User.getAttribute<String>("sub") ?: "unknown" // provider별로 userId 파싱

        val accessToken = tokenProvider.createAccessToken(userId)
        val refreshToken = tokenProvider.createRefreshToken(userId)

        response.addCookie(buildCookie("access_token", accessToken, maxAge = 600))   // 10분
        response.addCookie(buildCookie("refresh_token", refreshToken, maxAge = 1209600)) // 14일

        response.status = HttpStatus.OK.value()
        response.writer.write("""{"message": "Authenticated"}""")
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