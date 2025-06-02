package com.eatngo.order.usecase

import com.eatngo.order.event.PushEvent
import com.eatngo.order.infra.SsePersistence
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Service
class OrderSseService(
    private val ssePersistence: SsePersistence,
    private val objectMapper: ObjectMapper
) {
    fun findOrCreate(storeId: Long): SseEmitter {
        return ssePersistence.findOrCreate(storeId)
    }

    fun sendMessage(pushEvent: PushEvent<*>) =
        ssePersistence.findById(pushEvent.storeId)
            ?.send(
                SseEmitter.event()
                    .name(pushEvent.eventType.eventName)
                    .data(objectMapper.writeValueAsString(pushEvent.message))
            )
}