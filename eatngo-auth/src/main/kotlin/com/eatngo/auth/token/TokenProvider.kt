package com.eatngo.auth.token

import com.eatngo.auth.dto.LoginCustomer
import com.eatngo.auth.dto.LoginStoreOwner
import com.eatngo.auth.dto.LoginUser
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.SecretKey

const val REFRESH_TOKEN_TTL: Long = 1000L * 60 * 60  // 14 days in milliseconds
const val COOKIE_MAX_AGE: Long = 60 * 60 * 24 * 14 // 14 days
fun refreshKey(loginUser: LoginUser) = "refreshToken:${loginUser.getCurrentRole()}:${loginUser.userAccountId}"

@Component
class TokenProvider(
    @Value("\${jwt.secret}") private val secret: String,
    private val stringRedisTemplate: StringRedisTemplate,
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
            .claim("nickname", loginUser.nickname)
            .issuedAt(Date())
            .expiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
            .signWith(key)
            .compact()
    }

    fun createRefreshToken(loginUser: LoginUser): String {
        val customerId = if (loginUser is LoginCustomer) loginUser.customerId else null
        val storeOwnerId = if (loginUser is LoginStoreOwner) loginUser.storeOwnerId else null

        val refreshToken = Jwts.builder()
            .subject(loginUser.userAccountId.toString())
            .claim("roles", loginUser.roles)
            .claim("customerId", customerId)
            .claim("storeOwnerId", storeOwnerId)
            .claim("nickname", loginUser.nickname)
            .issuedAt(Date())
            .expiration(Date.from(Instant.now().plus(14, ChronoUnit.DAYS)))
            .signWith(key)
            .compact()

        stringRedisTemplate.opsForValue().set(refreshKey(loginUser), refreshToken, REFRESH_TOKEN_TTL) // 14 days in seconds
        return refreshToken
    }

    fun getAuthentication(token: String): Authentication {
        val claims = parseClaims(token)
        return buildAuthenticationFromClaims(claims)
    }

    fun getAuthenticationIgnoreExpired(token: String): Authentication {
        val claims = parseClaimsIgnoreExpired(token)
        return buildAuthenticationFromClaims(claims)
    }

    private fun buildAuthenticationFromClaims(claims: Claims): Authentication {
        val userAccountId = claims.subject.toLong()
        val roles = (claims["roles"] as? List<*>)?.map { it.toString() } ?: emptyList()
        val authorities = roles.map { SimpleGrantedAuthority(it) }

        val customerId = (claims["customerId"] as? Number)?.toLong()
        val storeOwnerId = (claims["storeOwnerId"] as? Number)?.toLong()
        val nickname = claims["nickname"] as? String

        val loginUser = when {
            customerId != null -> LoginCustomer(userAccountId, roles, nickname, customerId)
            storeOwnerId != null -> LoginStoreOwner(userAccountId, roles, nickname, storeOwnerId)
            else -> throw IllegalArgumentException("Invalid token claims: both customerId and storeOwnerId are null")
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
            throw e
        } catch (e: JwtException) {
            SecurityContextHolder.clearContext()
            throw IllegalArgumentException("Invalid JWT token", e)
        }
    }

    fun parseClaimsIgnoreExpired(token: String): Claims {
        return try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: ExpiredJwtException) {
            e.claims
        } catch (e: JwtException) {
            SecurityContextHolder.clearContext()
            throw IllegalArgumentException("Invalid JWT token", e)
        }
    }

    fun deleteRefreshToken(accessToken: String) =
        (getAuthentication(accessToken).principal as? LoginUser
            ?: throw IllegalArgumentException("Invalid access token"))
            .run {
                stringRedisTemplate.unlink(refreshKey(this))
            }

    fun getRefreshToken(accessToken: String): String? {
        val loginUser = getAuthenticationIgnoreExpired(accessToken).principal as? LoginUser
            ?: throw IllegalArgumentException("Invalid access token")

        return stringRedisTemplate.opsForValue().get(refreshKey(loginUser))
            ?.trim { it <= ' ' }
            ?: throw IllegalArgumentException("Refresh token not found for user: ${loginUser.userAccountId}")
    }

    fun createHttpOnlyCookie(name: String, value: String): ResponseCookie {
        return ResponseCookie
            .fromClientResponse(name, value)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("None")
            .maxAge(COOKIE_MAX_AGE)
            .build()
    }
}