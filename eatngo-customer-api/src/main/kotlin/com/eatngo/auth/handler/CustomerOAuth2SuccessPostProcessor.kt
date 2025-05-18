package com.eatngo.auth.handler

import com.eatngo.customer.domain.Customer
import com.eatngo.customer.infra.CustomerPersistence
import com.eatngo.user_account.infra.UserAccountPersistence
import org.springframework.stereotype.Component

@Component
class CustomerOAuth2SuccessPostProcessor(
    private val userAccountPersistence: UserAccountPersistence,
    private val customerPersistence: CustomerPersistence,
) : OAuth2SuccessPostProcessor {

    override fun postProcess(userId: Long) {
        customerPersistence.findByUserId(userId) ?: run {
            val userAccount = userAccountPersistence.getByIdOrThrow(userId)
            customerPersistence.save(Customer.create(userAccount))
        }
    }
}