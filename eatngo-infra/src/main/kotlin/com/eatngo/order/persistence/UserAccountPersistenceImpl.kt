package com.eatngo.order.persistence

import com.eatngo.user_account.domain.UserAccount
import com.eatngo.user_account.infra.UserAccountPersistence
import org.springframework.stereotype.Component

@Component
class UserAccountPersistenceImpl : UserAccountPersistence {
    override fun save(account: UserAccount): UserAccount {
        TODO("Not yet implemented")
    }

    override fun findById(id: Long): UserAccount? {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Long) {
        TODO("Not yet implemented")
    }

}