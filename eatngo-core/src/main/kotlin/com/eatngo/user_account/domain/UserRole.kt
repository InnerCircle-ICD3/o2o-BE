package com.eatngo.user_account.domain

import com.eatngo.user_account.oauth2.constants.Role
import java.time.LocalDateTime

class UserRole(
    val id: Long? = 0,
    val role: Role,
    var deletedAt: LocalDateTime? = null
) {
    fun delete() {
        deletedAt = LocalDateTime.now()
    }
}