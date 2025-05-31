package com.eatngo.subscription.notification

import com.eatngo.redis.utils.readValueFromJson
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.stereotype.Component

/**
 * Redis 구독 알림 리스너 설정
 */
@Configuration
class SubscriptionRedisListenerConfig {
    
    private val SUBSCRIPTION_NOTIFICATION_CHANNEL = "subscription:notifications"
    
    @Bean
    fun redisMessageListenerContainer(
        connectionFactory: RedisConnectionFactory,
        subscriptionMessageListener: SubscriptionRedisMessageListener
    ): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(connectionFactory)
        container.addMessageListener(
            MessageListenerAdapter(subscriptionMessageListener, "onMessage"),
            ChannelTopic(SUBSCRIPTION_NOTIFICATION_CHANNEL)
        )
        return container
    }
}

/**
 * Redis 구독 알림 메시지 리스너 구현
 */
@Component
class SubscriptionRedisMessageListener(
    private val objectMapper: ObjectMapper,
    private val notificationService: SubscriptionNotificationService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    /**
     * Redis 메시지 수신 시 호출되는 메서드
     */
    fun onMessage(message: String, pattern: String?) {
        try {
            // 메시지를 NotificationPayload로 변환
            val payload = objectMapper.readValueFromJson<SubscriptionNotificationService.NotificationPayload>(message)
            
            logger.info("구독 알림 수신: {}, 채널: {}", payload.type, pattern)
            
            // 해당 사용자 및 점주에게 SSE 알림 전송
            //TODO: 추가 로직 구현?
        } catch (e: Exception) {
            logger.error("구독 알림 처리 오류: {}", e.message)
        }
    }
} 