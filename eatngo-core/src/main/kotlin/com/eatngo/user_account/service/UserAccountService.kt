package com.eatngo.user_account.service

import com.eatngo.user_account.domain.UserAccount
import com.eatngo.user_account.infra.UserAccountPersistence
import com.eatngo.user_account.oauth2.dto.Oauth2
import org.springframework.stereotype.Service

@Service
class UserAccountService(
    private val userAccountPersistence: UserAccountPersistence
) {
    fun createAccount(oauth2: Oauth2): UserAccount {
        return userAccountPersistence.save(
            UserAccount.create(
                oauth2 = oauth2,
            )
        )
    }

    fun getAccountById(id: Long): UserAccount? {
        return userAccountPersistence.getByIdOrThrow(id)
    }

    fun deleteAccount(id: Long) {
        userAccountPersistence.deleteById(id)
    }
}