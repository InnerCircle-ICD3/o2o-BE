package com.eatngo.user_account.oauth2.constants

enum class Role(val roleName: String) {
    ANONYMOUS("ROLE_ANONYMOUS"),
    USER("ROLE_USER"),
    STORE_OWNER("ROLE_STORE_OWNER"),
    CUSTOMER("ROLE_CUSTOMER"),
    ADMIN("ROLE_ADMIN");
}