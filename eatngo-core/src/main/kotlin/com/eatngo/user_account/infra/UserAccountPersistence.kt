package com.eatngo.user_account.infra

import com.eatngo.user_account.domain.UserAccount
import com.eatngo.user_account.oauth2.constants.Oauth2Provider

interface UserAccountPersistence {

    fun save(account: UserAccount): UserAccount
    fun getByIdOrThrow(id: Long): UserAccount {
        return findById(id) ?: throw IllegalArgumentException("User account not found")
    }

    fun findById(id: Long): UserAccount?
    fun deleteById(id: Long)
    fun findByOauth(userKey: String, provider: Oauth2Provider): UserAccount?
}