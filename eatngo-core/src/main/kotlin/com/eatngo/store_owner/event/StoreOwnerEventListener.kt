package com.eatngo.customer.event

import com.eatngo.user_account.event.UserDeletedEvent
import com.eatngo.user_account.infra.UserAccountPersistence
import com.eatngo.user_account.oauth2.constants.Role
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class StoreOwnerEventListener(
    private val userAccountPersistence: UserAccountPersistence,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @Async
    @EventListener
    fun onCustomerDeleted(event: StoreOwnerDeletedEvent) {
        val userId = event.userId
        userAccountPersistence.findById(userId)?.let { userAccount ->
            userAccount.roles.find { it.role == Role.STORE_OWNER }?.delete()
            userAccountPersistence.save(userAccount)

            val liveRoles = userAccount.roles.filter { it.deletedAt == null }
            if (liveRoles.size == 1 && liveRoles.firstOrNull()?.role == Role.USER) {
                userAccountPersistence.deleteById(userId)
                applicationEventPublisher.publishEvent(
                    UserDeletedEvent(userAccount)
                )
            }
        }
    }
}