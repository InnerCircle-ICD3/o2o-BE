package com.eatngo.auth.filter

import com.eatngo.auth.constants.AuthenticationConstants.ACCESS_TOKEN
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
                // TODO redis refresh token 검사해서 access token 재발급하는 로직 추가하기
            } catch (e: Exception) {
                logger.debug("Invalid token: ${e.message}")
                SecurityContextHolder.clearContext()
            }
        }

        filterChain.doFilter(request, response)
    }
}