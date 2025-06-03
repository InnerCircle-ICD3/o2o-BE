package com.eatngo.auth.filter

import com.eatngo.auth.constants.AuthenticationConstants.ACCESS_TOKEN
import com.eatngo.auth.constants.AuthenticationConstants.SET_COOKIE_HEADER
import com.eatngo.auth.dto.LoginUser
import com.eatngo.auth.token.TokenProvider
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val tokenProvider: TokenProvider
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val accessToken = request.cookies?.firstOrNull { it.name == ACCESS_TOKEN }?.value

        if (!accessToken.isNullOrBlank()) {
            try {
                val authentication = tokenProvider.getAuthentication(accessToken)
                SecurityContextHolder.getContext().authentication = authentication
            } catch (e: ExpiredJwtException) {
                handleRefreshToken(response, accessToken)
            } catch (e: Exception) {
                logger.debug("Invalid token: ${e.message}")
                SecurityContextHolder.clearContext()
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun handleRefreshToken(response: HttpServletResponse, expiredAccessToken: String) {
        val refreshToken = tokenProvider.getRefreshToken(expiredAccessToken)
        if (refreshToken.isNullOrBlank()) {
            logger.debug("No refresh token found for expired access token.")
            return
        }

        try {
            val loginUser = tokenProvider.getAuthentication(refreshToken).principal as LoginUser
            val newAccessToken = tokenProvider.createAccessToken(loginUser)

            val responseCookie = tokenProvider.createHttpOnlyCookie(ACCESS_TOKEN, newAccessToken)
            response.addHeader(SET_COOKIE_HEADER, responseCookie.toString())

            val newAuth = tokenProvider.getAuthentication(newAccessToken)
            SecurityContextHolder.getContext().authentication = newAuth

            logger.debug("Access token refreshed successfully.")
        } catch (e: Exception) {
            logger.warn("Failed to authenticate with refresh token: ${e.message}")
            SecurityContextHolder.clearContext()
        }
    }
}