package com.eatngo.oauth2

import com.eatngo.auth.constants.Role.*
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService : OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val delegate = DefaultOAuth2UserService()
        val oAuth2User = delegate.loadUser(userRequest)

        val registrationId = userRequest.clientRegistration.registrationId // ex: google, kakao
        val attributes = oAuth2User.attributes

        println("OAuth provider: $registrationId")
        println("User attributes: $attributes")

        val email = oAuth2User.getAttribute<String>("email") ?: throw Exception("no email")
        val userKey = oAuth2User.getAttribute<String>("id") ?: throw Exception("no id")

        // 사용자 DB에서 userId 조회 후 사용자 타입 판별 (ex: 고객 / 점주 / 관리자)
        // val userType = userRepository.findByOauthId(userId)?.type  // 예: "ADMIN", "STORE_OWNER" 등 // TODO 도메인 로직 연동하기
        val userType = USER
        val roles = when (userType) {
            ADMIN -> listOf(USER, STORE_OWNER, CUSTOMER, ADMIN)
            STORE_OWNER -> listOf(USER, STORE_OWNER)
            CUSTOMER -> listOf(USER, CUSTOMER)
            else -> listOf(USER)
        }

        val authorities = roles.map { SimpleGrantedAuthority(it.roleName) }

        return DefaultOAuth2User(
            authorities.toSet(),
            oAuth2User.attributes,
            "id"
        )
    }
}