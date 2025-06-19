package com.eatngo.oauth2

import com.eatngo.auth.constants.AuthenticationConstants.PRINCIPAL_KEY
import com.eatngo.user_account.domain.UserAccount
import com.eatngo.user_account.infra.UserAccountPersistence
import com.eatngo.user_account.oauth2.constants.OAuth2Provider
import com.eatngo.user_account.oauth2.constants.Role
import com.eatngo.user_account.oauth2.constants.Role.*
import com.eatngo.user_account.oauth2.domain.UserAccountOAuth2
import com.eatngo.user_account.oauth2.dto.KakaoOAuth2
import com.eatngo.user_account.oauth2.dto.OAuth2
import com.eatngo.user_account.vo.EmailAddress
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import java.time.ZoneId

@Service
class CustomOAuth2UserService(
    private val userAccountPersistence: UserAccountPersistence,
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val delegate = DefaultOAuth2UserService()
        val oAuth2User = delegate.loadUser(userRequest)

        val provider = OAuth2Provider.valueOfIgnoreCase(userRequest.clientRegistration.registrationId)
        val token = userRequest.accessToken
        val oAuth2: OAuth2 = handleOauth2Attributes(provider, oAuth2User, token)

        val userKey = oAuth2.id.toString()
        val userAccount = (userAccountPersistence.findByOauth(userKey, provider)
            .also {
                it?.updateOauth2(
                    UserAccountOAuth2.of(
                        account = it,
                        oAuth2 = oAuth2
                    )
                )
            }
            ?: oAuth2.email
                ?.let { userAccountPersistence.findByEmail(EmailAddress(it)) }
                ?.also {
                    it.addOauth2(
                        UserAccountOAuth2.of(
                            account = it,
                            oAuth2 = oAuth2
                        )
                    )
                })
            ?: UserAccount.create(oAuth2)
        val savedAccount = userAccountPersistence.save(userAccount)

        val roles = handleRoles(savedAccount)
            .map { SimpleGrantedAuthority(it.toString()) }

        val oAuth2UserAttributesMap = oAuth2User.attributes.toMutableMap()
        oAuth2UserAttributesMap.put(PRINCIPAL_KEY, savedAccount.id.toString())

        return DefaultOAuth2User(
            roles,
            oAuth2UserAttributesMap,
            PRINCIPAL_KEY
        )

    }

    private fun handleRoles(userAccount: UserAccount): List<Role> =
        userAccount.roles.flatMap {
            when (it.role) {
                ADMIN -> listOf(USER, STORE_OWNER, CUSTOMER, ADMIN)
                STORE_OWNER -> listOf(USER, STORE_OWNER)
                CUSTOMER -> listOf(USER, CUSTOMER)
                else -> listOf(USER)
            }
        }.distinct()

    private fun handleOauth2Attributes(
        provider: OAuth2Provider,
        oAuth2User: OAuth2User,
        token: OAuth2AccessToken
    ) = when (provider) {
        OAuth2Provider.KAKAO -> KakaoOAuth2(
            oAuth2User.attributes, provider, token.tokenValue,
            token.expiresAt?.atZone(ZoneId.of("Asia/Seoul"))?.toLocalDateTime(),
            token.scopes.joinToString(",")
        )

        else -> throw IllegalArgumentException("Unsupported provider: $provider")
    }
}
