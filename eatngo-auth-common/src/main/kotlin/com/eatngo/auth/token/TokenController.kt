package com.eatngo.auth.token

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/token")
class TokenController(
    private val tokenProvider: TokenProvider
) {

    @PostMapping("/refresh")
    fun refreshToken(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<String> { // TODO 응답 포맷 변경
        val refreshToken = request.cookies?.firstOrNull { it.name == "refresh_token" }?.value
            ?: return ResponseEntity.status(401).body("No Refresh Token") // TODO 응답 포맷 변경

        return try {
            val userId = tokenProvider.getUserIdFromToken(refreshToken)
            val newAccessToken = tokenProvider.createAccessToken(userId)

            response.addCookie(
                Cookie("access_token", newAccessToken).apply {
                    isHttpOnly = true
                    secure = true
                    path = "/"
                    maxAge = 60 * 10 * 60 // 1 hour
                }
            )

            ResponseEntity.ok("Access token refreshed") // TODO 응답 포맷 변경

        } catch (e: Exception) {
            ResponseEntity.status(401).body("Invalid refresh token") // TODO 응답 포맷 변경
        }
    }
}