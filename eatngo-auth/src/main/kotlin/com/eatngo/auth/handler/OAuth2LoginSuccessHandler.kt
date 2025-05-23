package com.eatngo.auth.handler

import com.eatngo.auth.constants.AuthenticationConstants.ACCESS_TOKEN
import com.eatngo.auth.constants.AuthenticationConstants.PRINCIPAL_KEY
import com.eatngo.auth.token.TokenProvider
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
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
        val attributes = (authentication.principal as OAuth2User).attributes
        val userId = attributes[PRINCIPAL_KEY]?.toString()?.toLongOrNull()
            ?: throw IllegalArgumentException("User ID not found")

        val loginUser = postProcessor.postProcess(userId)
        val accessToken = tokenProvider.createAccessToken(loginUser)

        val responseCookie = createHttpOnlyCookie(ACCESS_TOKEN, accessToken)
        response.addHeader("Set-Cookie", responseCookie.toString())
        // TODO redis refresh token 추가하기
        response.contentType = "application/json"
    }

    private fun createHttpOnlyCookie(name: String, value: String): ResponseCookie {
        return ResponseCookie
            .fromClientResponse(name, value)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("None")
            .maxAge(60 * 60 * 24 * 14) // 14 days
            .build()
    }
}