package com.eatngo.auth.handler

import com.eatngo.auth.dto.LoginUser
import com.eatngo.user_account.oauth2.constants.Role
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean

fun interface OAuth2SuccessPostProcessor {

    fun postProcess(
        userId: Long
    ): LoginUser
}

@ConditionalOnMissingBean(OAuth2SuccessPostProcessor::class)
class DefaultOAuth2SuccessPostProcessor : OAuth2SuccessPostProcessor {

    override fun postProcess(userId: Long): LoginUser {
        return object : LoginUser {
            override fun getCurrentRole(): Role {
                return Role.USER
            }

            override val userAccountId: Long = userId
            override val nickname: String? = null
            override val roles: List<String> = emptyList()
        }
    }
}