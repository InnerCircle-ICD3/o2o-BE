package com.eatngo.notification.service

import com.eatngo.notification.event.NotificationEvent
import com.eatngo.notification.event.NotificationEventType
import com.eatngo.notification.infra.NotificationPersistence
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Service
class NotificationSseService(
    private val notificationPersistence: NotificationPersistence,
    private val objectMapper: ObjectMapper,
) {
    fun findOrCreate(storeId: Long) = notificationPersistence.findOrCreate(storeId)

    fun sendMessage(notificationEvent: NotificationEvent<*>) =
        notificationPersistence
            .findById(notificationEvent.storeId)
            ?.runCatching {
                send(
                    SseEmitter
                        .event()
                        .name(notificationEvent.eventType.eventName)
                        .data(objectMapper.writeValueAsString(notificationEvent.message)),
                )
            }?.onFailure {
                notificationPersistence.remove(notificationEvent.storeId)
            }

    fun sendHeartbeat() =
        notificationPersistence
            .getAll()
            .forEach { (storeId, sseEmitter) ->
                runCatching {
                    sseEmitter.send(
                        SseEmitter
                            .event()
                            .name(NotificationEventType.HEARTBEAT.eventName),
                    )
                }.onFailure { notificationPersistence.remove(storeId) }
            }

    companion object {
        const val TIME_OUT_VALUE = 600_000L // 10 minute
    }
}
