package com.eatngo.auth.filter

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
        val accessToken = request.cookies?.firstOrNull { it.name == "access_token" }?.value

        if (!accessToken.isNullOrBlank()) {
            try {
                val authentication = tokenProvider.getAuthentication(accessToken)
                SecurityContextHolder.getContext().authentication = authentication
            } catch (e: ExpiredJwtException) {
                // do nothing - 처리 흐름은 refresh 로 넘긴다
            } catch (e: Exception) {
                // invalid token
            }
        }

        filterChain.doFilter(request, response)
    }
}