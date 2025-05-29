package com.eatngo.user_account.service

import com.eatngo.user_account.domain.UserAccount
import com.eatngo.user_account.infra.UserAccountPersistence
import com.eatngo.user_account.oauth2.dto.OAuth2
import org.springframework.stereotype.Service

@Service
class UserAccountService(
    private val userAccountPersistence: UserAccountPersistence
) {
    fun createAccount(oAuth2: OAuth2): UserAccount {
        return userAccountPersistence.save(
            UserAccount.create(
                oAuth2 = oAuth2,
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