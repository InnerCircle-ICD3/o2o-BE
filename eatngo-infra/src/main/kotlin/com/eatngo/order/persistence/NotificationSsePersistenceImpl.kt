package com.eatngo.order.persistence

import com.eatngo.notification.infra.NotificationPersistence
import com.eatngo.notification.service.NotificationSseService.Companion.TIME_OUT_VALUE
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
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
                }.also { sseEmitter -> sseEmitter.send(SseEmitter.event().name("CONNECTED")) }
        }

    override fun findById(storeId: Long) = emitters.getOrElse(storeId) { null }
}
