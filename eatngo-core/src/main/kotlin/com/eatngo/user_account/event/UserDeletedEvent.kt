package com.eatngo.user_account.event

import com.eatngo.user_account.domain.UserAccount

data class UserDeletedEvent(
    val userAccount: UserAccount
)