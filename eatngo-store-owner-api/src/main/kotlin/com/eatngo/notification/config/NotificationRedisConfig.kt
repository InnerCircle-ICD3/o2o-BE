package com.eatngo.notification.config


import com.eatngo.notification.NotificationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer

@Configuration
class NotificationRedisConfig(
    private val redisConnectionFactory: RedisConnectionFactory,
    private val notificationListener: NotificationListener
) {

    @Bean
    fun listenerContainer(): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.connectionFactory = redisConnectionFactory
        container.addMessageListener(notificationListener, ChannelTopic(NotificationConfig.SSE_TOPIC))
        return container
    }
}