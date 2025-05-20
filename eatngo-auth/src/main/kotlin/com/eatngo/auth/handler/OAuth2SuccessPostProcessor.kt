package com.eatngo.auth.handler

import com.eatngo.auth.dto.LoginUser
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
            override val userAccountId: Long = userId
            override val roles: List<String> = emptyList()
        }
    }
}