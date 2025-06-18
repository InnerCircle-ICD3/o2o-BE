package com.eatngo.order.persistence

import com.eatngo.notification.event.NotificationEventType
import com.eatngo.notification.infra.NotificationPersistence
import com.eatngo.notification.service.NotificationSseService.Companion.TIME_OUT_VALUE
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

@Component
class NotificationSsePersistenceImpl : NotificationPersistence {
    private val emitters = ConcurrentHashMap<Long, SseEmitter>()

    override fun findOrCreate(storeId: Long) =
        emitters.computeIfAbsent(storeId) {
            SseEmitter(TIME_OUT_VALUE)
                .apply {
                    onTimeout { emitters.remove(storeId) }
                    onCompletion { emitters.remove(storeId) }
                    onError { emitters.remove(storeId) }
                }.also { sseEmitter ->
                    try {
                        sseEmitter.send(
                            SseEmitter.event().name(NotificationEventType.CONNECTED.eventName),
                        )
                    } catch (e: IOException) {
                        emitters.remove(storeId)
                    }
                }
        }

    override fun findById(storeId: Long) = emitters.getOrElse(storeId) { null }

    override fun getAll() = emitters.toMap()

    override fun remove(storeId: Long) {
        emitters.remove(storeId)
    }
}
