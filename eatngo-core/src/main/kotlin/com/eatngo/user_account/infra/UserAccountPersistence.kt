package com.eatngo.user_account.infra

import com.eatngo.common.exception.user.UserAccountException
import com.eatngo.user_account.domain.UserAccount
import com.eatngo.user_account.oauth2.constants.Oauth2Provider
import com.eatngo.user_account.vo.EmailAddress

interface UserAccountPersistence {

    fun save(account: UserAccount): UserAccount
    fun getByIdOrThrow(id: Long): UserAccount {
        return findById(id) ?: throw UserAccountException.UserAccountNotfoundException(id)
    }

    fun findById(id: Long): UserAccount?
    fun deleteById(id: Long)
    fun findByOauth(userKey: String, provider: Oauth2Provider): UserAccount?
    fun findByEmail(emailAddress: EmailAddress): UserAccount?
}