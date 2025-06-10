package com.eatngo.customer.event

import com.eatngo.user_account.event.UserDeletedEvent
import com.eatngo.user_account.infra.UserAccountPersistence
import com.eatngo.user_account.oauth2.constants.Role
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionalEventListener

@Component
@Transactional
class StoreOwnerEventListener(
    private val userAccountPersistence: UserAccountPersistence,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @Async
    @TransactionalEventListener
    fun onCustomerDeleted(event: StoreOwnerDeletedEvent) {
        val userId = event.userId
        userAccountPersistence.findById(userId)?.let { userAccount ->
            if (userAccount.roles.isEmpty()) {
                userAccountPersistence.deleteById(userId)
                applicationEventPublisher.publishEvent(
                    UserDeletedEvent(userAccount)
                )
            } else {
                userAccount.roles.find { it.role == Role.STORE_OWNER }?.let {
                    userAccount.roles.remove(it)
                }
                userAccountPersistence.save(userAccount)
            }
        }
    }
}