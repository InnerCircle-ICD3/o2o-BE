package com.eatngo.user_account.oauth2.constants

enum class OAuth2Provider {
    KAKAO;


    companion object {
        fun valueOfIgnoreCase(name: String?) = values().firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: throw IllegalArgumentException("Invalid provider name: $name")
    }
}