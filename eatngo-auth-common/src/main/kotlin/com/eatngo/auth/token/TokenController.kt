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
    ): ResponseEntity<String> {
        val refreshToken = request.cookies?.firstOrNull { it.name == "refresh_token" }?.value
            ?: return ResponseEntity.status(401).body("No Refresh Token")

        return try {
            val userId = tokenProvider.getUserIdFromToken(refreshToken)
            val newAccessToken = tokenProvider.createAccessToken(userId)

            response.addCookie(
                Cookie("access_token", newAccessToken).apply {
                    isHttpOnly = true
                    secure = true
                    path = "/"
                    maxAge = 600
                }
            )

            ResponseEntity.ok("Access token refreshed")

        } catch (e: Exception) {
            ResponseEntity.status(401).body("Invalid refresh token")
        }
    }
}