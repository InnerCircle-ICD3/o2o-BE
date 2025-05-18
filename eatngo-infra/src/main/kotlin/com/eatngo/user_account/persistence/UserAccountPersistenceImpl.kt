package com.eatngo.user_account.persistence

import com.eatngo.user_account.domain.UserAccount
import com.eatngo.user_account.infra.UserAccountPersistence
import com.eatngo.user_account.oauth2.constants.Oauth2Provider
import org.springframework.stereotype.Component

@Component
class UserAccountPersistenceImpl : UserAccountPersistence {

    override fun save(account: UserAccount): UserAccount {
        // TODO : Implement save logic
        return account
    }

    override fun findById(id: Long): UserAccount? {
        // TODO : Implement findById logic
        return null
    }

    override fun deleteById(id: Long) {
        // TODO : Implement deleteById logic
    }

    override fun findByOauth(userKey: String, provider: Oauth2Provider): UserAccount? {
        TODO("Not yet implemented")
    }
}