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
import java.time.Duration

@Configuration
@EnableRedisRepositories
class RedisConfig(
    private val env: Environment,
) {
    val stringSerializer: StringRedisSerializer = StringRedisSerializer()

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val host = env.getProperty("spring.data.redis.host") ?: "localhost"
        val port = env.getProperty("spring.data.redis.port")?.toInt() ?: 6379
        val password = env.getProperty("spring.data.redis.password")

        val lettuceClientConfiguration =
            LettuceClientConfiguration
                .builder()
                .commandTimeout(Duration.ofSeconds(5))
                .shutdownTimeout(Duration.ZERO)
                .build()
        val redisStandaloneConfiguration = RedisStandaloneConfiguration(host, port)
        redisStandaloneConfiguration.setPassword(password)

        return LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration)
    }

    @Bean(value = ["redisTemplate"])
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<ByteArray?, ByteArray?> {
        val template = RedisTemplate<ByteArray?, ByteArray?>()
        template.connectionFactory = redisConnectionFactory
        template.keySerializer = stringSerializer
        template.hashKeySerializer = stringSerializer
        return template
    }
}
