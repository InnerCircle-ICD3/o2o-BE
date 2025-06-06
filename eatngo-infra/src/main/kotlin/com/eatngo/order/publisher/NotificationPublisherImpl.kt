package com.eatngo.order.publisher

import com.eatngo.notification.config.NotificationConfig
import com.eatngo.notification.event.NotificationEvent
import com.eatngo.notification.infra.NotificationPublisher
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class NotificationPublisherImpl(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper
) : NotificationPublisher {
    override fun publish(notificationEvent: NotificationEvent<*>): Long =
        redisTemplate.convertAndSend(NotificationConfig.SSE_TOPIC, objectMapper.writeValueAsString(notificationEvent))

}