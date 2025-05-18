package com.eatngo.auth.handler

import org.springframework.stereotype.Component

fun interface OAuth2SuccessPostProcessor {

    fun postProcess(
        userId: Long
    )
}

@Component
class DefaultOAuth2SuccessPostProcessor : OAuth2SuccessPostProcessor {

    override fun postProcess(userId: Long) {
        // No-op
    }
}