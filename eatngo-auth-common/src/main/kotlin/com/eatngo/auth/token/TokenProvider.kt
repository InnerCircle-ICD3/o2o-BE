package com.eatngo.auth.token

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class TokenProvider(
    @Value("\${jwt.secret}") private val secret: String
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret))

    fun createAccessToken(userId: String): String {
        return Jwts.builder()
            .setSubject(userId)
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 10 * 6)) // 1 hour
            .signWith(key)
            .compact()
    }

    fun createRefreshToken(userId: String): String {
        return Jwts.builder()
            .setSubject(userId)
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 14)) // 14 days
            .signWith(key)
            .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val claims = parseClaims(token)
        val userId = claims.subject
        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
        return UsernamePasswordAuthenticationToken(userId, null, authorities)
    }

    fun getUserIdFromToken(token: String): String {
        return parseClaims(token).subject
    }

    fun parseClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }
}