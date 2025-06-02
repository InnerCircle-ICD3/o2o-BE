package com.eatngo.order.infra

import com.eatngo.order.event.PushEvent

interface MessagePublisher {
    fun publish(pushEvent: PushEvent<*>)
}