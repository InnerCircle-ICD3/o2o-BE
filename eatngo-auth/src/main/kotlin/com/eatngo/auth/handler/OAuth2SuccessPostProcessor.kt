package com.eatngo.auth.handler

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean

fun interface OAuth2SuccessPostProcessor {

    fun postProcess(
        userId: Long
    )
}

@ConditionalOnMissingBean(OAuth2SuccessPostProcessor::class)
class DefaultOAuth2SuccessPostProcessor : OAuth2SuccessPostProcessor {

    override fun postProcess(userId: Long) {
        // No-op
    }
}