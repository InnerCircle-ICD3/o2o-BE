package com.eatngo.auth.constants

enum class Role(val roleName: String) {
    USER("ROLE_USER"),
    STORE_OWNER("ROLE_STORE_OWNER"),
    CUSTOMER("ROLE_CUSTOMER"),
    ADMIN("ROLE_ADMIN");
}