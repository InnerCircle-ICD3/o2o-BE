package com.eatngo.notification

import com.eatngo.notification.event.NotificationEvent
import com.eatngo.notification.event.NotificationEventType
import com.eatngo.notification.service.NotificationSseService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.connection.Message

import org.springframework.stereotype.Component
import org.springframework.data.redis.connection.MessageListener

@Component
class NotificationListener(
    private val notificationSseService: NotificationSseService,
    private val objectMapper: ObjectMapper
) : MessageListener {
    override fun onMessage(message: Message, pattern: ByteArray?) {
        val rawJson = String(message.body)
        val root = objectMapper.readTree(rawJson)
        val eventNameStr = root.get("eventName").asText()

        val eventType = NotificationEventType.entries
            .find { it.eventName == eventNameStr }
            ?: throw IllegalArgumentException("Unknown event type: $eventNameStr")

        val messageNode = root.get("message")
        val convertedMessage = objectMapper.treeToValue(messageNode, eventType.messageClass)

        val notificationEvent = NotificationEvent(
            storeId = root.get("storeId").asLong(),
            eventType = eventType,
            message = convertedMessage
        )
        notificationSseService.sendMessage(notificationEvent)
    }
}