package com.eatngo.auth.event

import com.eatngo.oauth2.OAuth2Service
import com.eatngo.user_account.event.UserDeletedEvent
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class UserDeletedEventListener(
    private val oauth2Services: Set<OAuth2Service>,
) {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onUserDeleted(event: UserDeletedEvent) {
        event.userAccount.oAuth2.forEach { oauth2 ->
            oauth2Services.find {
                it.supports(oauth2.provider)
            }?.unlink(oauth2.accessToken)
        }
    }
}