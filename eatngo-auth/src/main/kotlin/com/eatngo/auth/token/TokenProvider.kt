package com.eatngo.auth.token

import com.eatngo.auth.dto.LoginCustomer
import com.eatngo.auth.dto.LoginStoreOwner
import com.eatngo.auth.dto.LoginUser
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

    fun createAccessToken(loginUser: LoginUser): String {
        val customerId = if (loginUser is LoginCustomer) loginUser.customerId else null
        val storeOwnerId = if (loginUser is LoginStoreOwner) loginUser.storeOwnerId else null

        return Jwts.builder()
            .subject(loginUser.userAccountId.toString())
            .claim("roles", loginUser.roles)
            .claim("customerId", customerId)
            .claim("storeOwnerId", storeOwnerId)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
            .signWith(key)
            .compact()
    }

    fun createRefreshToken(loginUser: LoginUser): String {
        val customerId = if (loginUser is LoginCustomer) loginUser.customerId else null
        val storeOwnerId = if (loginUser is LoginStoreOwner) loginUser.storeOwnerId else null

        return Jwts.builder()
            .subject(loginUser.userAccountId.toString())
            .claim("roles", loginUser.roles)
            .claim("customerId", customerId)
            .claim("storeOwnerId", storeOwnerId)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 14)) // 14 days
            .signWith(key)
            .compact()
    }

    @Suppress("UNCHECKED_CAST")
    fun getAuthentication(token: String): Authentication {
        val claims = parseClaims(token)

        val userAccountId = claims.subject.toLong()
        val roles = claims["roles"] as? List<*> ?: emptyList<Any>()
        val authorities = roles.map { SimpleGrantedAuthority(it.toString()) }

        val customerId = (claims["customerId"] as? Number)?.toLong()
        val storeOwnerId = (claims["storeOwnerId"] as? Number)?.toLong()

        val loginUser: LoginUser = when {
            customerId != null -> LoginCustomer(
                userAccountId = userAccountId,
                roles = roles.map { it.toString() },
                customerId = customerId
            )

            storeOwnerId != null -> LoginStoreOwner(
                userAccountId = userAccountId,
                roles = roles.map { it.toString() },
                storeOwnerId = storeOwnerId
            )

            else -> throw IllegalArgumentException("Invalid token claims")
        }

        return UsernamePasswordAuthenticationToken(loginUser, null, authorities)
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