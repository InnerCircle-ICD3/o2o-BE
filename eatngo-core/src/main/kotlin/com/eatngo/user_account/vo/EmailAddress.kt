package com.eatngo.user_account.vo

@JvmInline
value class EmailAddress(val value: String) {
    init {
        require(value.isNotBlank()) { "Email address cannot be blank" }
        require(value.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))) {
            "Invalid email address format"
        }
    }

    override fun toString(): String {
        return value
    }

    companion object {
        fun from(email: String): EmailAddress {
            return EmailAddress(email)
        }
    }
}
