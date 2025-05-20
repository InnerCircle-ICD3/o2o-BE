package com.eatngo.oauth2

import com.eatngo.auth.constants.AuthenticationConstants.PRINCIPAL_KEY
import com.eatngo.user_account.domain.UserAccount
import com.eatngo.user_account.infra.UserAccountPersistence
import com.eatngo.user_account.oauth2.constants.Oauth2Provider
import com.eatngo.user_account.oauth2.constants.Role
import com.eatngo.user_account.oauth2.constants.Role.*
import com.eatngo.user_account.oauth2.dto.KakaoOauth2
import com.eatngo.user_account.oauth2.dto.Oauth2
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    private val userAccountPersistence: UserAccountPersistence,
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val delegate = DefaultOAuth2UserService()
        val oAuth2User = delegate.loadUser(userRequest)

        val provider = Oauth2Provider.valueOfIgnoreCase(userRequest.clientRegistration.registrationId)
        val oauth2: Oauth2 = handleOauth2Attributes(provider, oAuth2User)

        val userAccount = userAccountPersistence.findByOauth(oauth2.id.toString(), provider)
            ?: userAccountPersistence.save(UserAccount.create(oauth2))
        // TODO term fetch api 추가하기

        val roles = handleRoles(userAccount)
            .map { SimpleGrantedAuthority(it.toString()) }

        val oAuth2UserAttributesMap = oAuth2User.attributes.toMutableMap()
        oAuth2UserAttributesMap.put(PRINCIPAL_KEY, userAccount.id.toString())

        return DefaultOAuth2User(
            roles,
            oAuth2UserAttributesMap,
            PRINCIPAL_KEY
        )

    }

    private fun handleRoles(userAccount: UserAccount): List<Role> =
        userAccount.roles.flatMap {
            when (it) {
                ADMIN -> listOf(USER, STORE_OWNER, CUSTOMER, ADMIN)
                STORE_OWNER -> listOf(USER, STORE_OWNER)
                CUSTOMER -> listOf(USER, CUSTOMER)
                else -> listOf(USER)
            }
        }.distinct()

    private fun handleOauth2Attributes(
        provider: Oauth2Provider,
        oAuth2User: OAuth2User
    ) = when (provider) {
        Oauth2Provider.KAKAO -> KakaoOauth2(oAuth2User.attributes, provider)
        else -> throw IllegalArgumentException("Unsupported provider: $provider")
    }
}
