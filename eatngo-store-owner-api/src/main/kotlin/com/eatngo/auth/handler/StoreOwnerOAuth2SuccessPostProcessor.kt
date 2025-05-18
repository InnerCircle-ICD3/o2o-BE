package com.eatngo.auth.handler

import com.eatngo.store_owner.domain.StoreOwner
import com.eatngo.store_owner.infra.StoreOwnerPersistence
import com.eatngo.user_account.infra.UserAccountPersistence
import org.springframework.stereotype.Component

@Component
class StoreOwnerOAuth2SuccessPostProcessor(
    private val userAccountPersistence: UserAccountPersistence,
    private val storeOwnerPersistence: StoreOwnerPersistence,
) : OAuth2SuccessPostProcessor {

    override fun postProcess(userId: Long) {
        storeOwnerPersistence.findByUserId(userId)
            ?: {
                userAccountPersistence.getByIdOrThrow(userId)
                    .let { userAccount ->
                        StoreOwner.create(userAccount)
                    }.let { customer ->
                        storeOwnerPersistence.save(customer)
                    }
            }
    }
}