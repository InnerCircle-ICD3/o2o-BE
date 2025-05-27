package com.eatngo.user_account.vo

@JvmInline
value class Nickname(val value: String) {
    init {
        require(value.isNotBlank()) { "Email address cannot be blank" }
        require(value.length <= 50) { "Nickname cannot exceed 50 characters" }
    }

    override fun toString(): String {
        return value
    }

}
