package com.eatngo.redis

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableRedisRepositories
class RedisConfig (
    private val env: Environment
) {

    val stringSerializer: StringRedisSerializer = StringRedisSerializer()

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val host = env.getProperty("spring.redis.host")?: "localhost"
        val port = env.getProperty("spring.redis.port")?.toInt() ?: 6379

        val lettuceClientConfiguration = LettuceClientConfiguration.builder()
            .build()
        val redisStandaloneConfiguration = RedisStandaloneConfiguration(/* hostName = */ host, /* port = */ port)

        return LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration)
    }

    @Bean(value = ["redisTemplate"])
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<Any, Any> {
        val template = RedisTemplate<Any, Any>()
        template.connectionFactory = redisConnectionFactory
        template.keySerializer  = stringSerializer
        template.hashKeySerializer  = stringSerializer
        return template
    }
}