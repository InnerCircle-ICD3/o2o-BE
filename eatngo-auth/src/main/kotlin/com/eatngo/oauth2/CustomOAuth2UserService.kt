package com.eatngo.oauth2

import com.eatngo.user_account.domain.UserAccount
import com.eatngo.user_account.oauth2.constants.Oauth2Provider
import com.eatngo.user_account.oauth2.constants.Role.*
import com.eatngo.user_account.oauth2.dto.Oauth2
import com.eatngo.user_account.persistence.UserAccountPersistenceImpl
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    private val userAccountPersistence: UserAccountPersistenceImpl,
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val delegate = DefaultOAuth2UserService()
        val oAuth2User = delegate.loadUser(userRequest)

        val provider = Oauth2Provider.valueOf(userRequest.clientRegistration.registrationId)
        val attributes = oAuth2User.attributes as Oauth2

        val userKey = oAuth2User.getAttribute<String>("id") ?: throw Exception("no id")

        val userAccount = userAccountPersistence.findByOauth(userKey, provider)
            ?: userAccountPersistence.save(UserAccount.create(attributes))

        val userType = USER
        val roles = when (userType) {
            ADMIN -> listOf(USER, STORE_OWNER, CUSTOMER, ADMIN)
            STORE_OWNER -> listOf(USER, STORE_OWNER)
            CUSTOMER -> listOf(USER, CUSTOMER)
            else -> listOf(USER)
        }

        val authorities = roles.map { SimpleGrantedAuthority(it.roleName) }
        val userNameAttributeName = userRequest.clientRegistration.providerDetails
            .userInfoEndpoint.userNameAttributeName
        return DefaultOAuth2User(
            authorities.toSet(),
            oAuth2User.attributes,
            userNameAttributeName
        )
    }
}