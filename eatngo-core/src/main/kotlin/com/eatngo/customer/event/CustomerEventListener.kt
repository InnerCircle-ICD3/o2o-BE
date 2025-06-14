package com.eatngo.customer.event

import com.eatngo.user_account.event.UserDeletedEvent
import com.eatngo.user_account.infra.UserAccountPersistence
import com.eatngo.user_account.oauth2.constants.Role
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class CustomerEventListener(
    private val userAccountPersistence: UserAccountPersistence,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onCustomerDeleted(event: CustomerDeletedEvent) {
        val userId = event.userId
        userAccountPersistence.findById(userId)?.let { userAccount ->
            val roles = userAccount.roles
            roles.find { it.role == Role.CUSTOMER }?.let {
                roles.remove(it)
            }
            userAccountPersistence.save(userAccount)

            if (roles.isEmpty()) {
                userAccountPersistence.deleteById(userId)
                applicationEventPublisher.publishEvent(
                    UserDeletedEvent(userAccount)
                )
            }
        }
    }
}