package com.eatngo.auth.handler

import com.eatngo.auth.token.TokenProvider
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2LoginSuccessHandler(
    private val tokenProvider: TokenProvider,
    private val postProcessor: OAuth2SuccessPostProcessor // 다형성 지원
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val userId = authentication.principal as Long

        postProcessor.postProcess(userId)

        val accessToken = tokenProvider.createAccessToken(userId)

        response.addCookie(createHttpOnlyCookie("accessToken", accessToken))
        response.contentType = "application/json"
    }

    private fun createHttpOnlyCookie(name: String, value: String): Cookie {
        return Cookie(name, value).apply {
            isHttpOnly = true
            path = "/"
            maxAge = 60 * 60 // 1 hour
            secure = true
        }
    }
}