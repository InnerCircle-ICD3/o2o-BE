package com.eatngo.notification.listener

import com.eatngo.order.event.OrderEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class OrderNotificationEventListener {
    @EventListener
    @Async
    fun handle(event: OrderEvent){

    }
}