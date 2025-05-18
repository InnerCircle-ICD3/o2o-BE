package com.eatngo.auth.token

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class TokenProvider(
    @Value("\${jwt.secret}") private val secret: String
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret))

    fun createAccessToken(userId: Long): String {
        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
            .signWith(key)
            .compact()
    }

    fun createRefreshToken(userId: Long): String {
        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 14)) // 14 days
            .signWith(key)
            .compact()
    }

    @Suppress("UNCHECKED_CAST")
    fun getAuthentication(token: String): Authentication {
        val claims = parseClaims(token)
        val userId = claims.subject

        val roles = claims["roles"] as? List<String> ?: listOf("ROLE_USER")
        val authorities = roles.map { SimpleGrantedAuthority(it) }

        return UsernamePasswordAuthenticationToken(userId, null, authorities)
    }

    fun parseClaims(token: String): Claims {
        return try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: ExpiredJwtException) {
            e.claims // 만료됐지만 claims만 필요할 경우
        } catch (e: JwtException) {
            SecurityContextHolder.clearContext()
            throw IllegalArgumentException("Invalid JWT token", e)
        }
    }
}