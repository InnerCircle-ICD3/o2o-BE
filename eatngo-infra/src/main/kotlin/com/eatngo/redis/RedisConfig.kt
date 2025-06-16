package com.eatngo.redis

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.env.Environment
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
@EnableRedisRepositories(
    basePackages = ["com.eatngo.redis.repository"]
)
@EnableCaching
class RedisConfig(
    private val env: Environment,
    private val objectMapper: ObjectMapper
) {
    val stringSerializer: StringRedisSerializer = StringRedisSerializer()

    @Primary
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

    @Bean
    fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory {
        val host = env.getProperty("spring.data.redis.host") ?: "localhost"
        val port = env.getProperty("spring.data.redis.port")?.toInt() ?: 6379
        val password = env.getProperty("spring.data.redis.password")

        val lettuceClientConfig = LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ofSeconds(5))
            .shutdownTimeout(Duration.ZERO)
            .build()
        val standaloneConfig = RedisStandaloneConfiguration(host, port).apply {
            setPassword(password)
        }
        return LettuceConnectionFactory(standaloneConfig, lettuceClientConfig)
    }

    @Bean(value = ["redisTemplate"])
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<ByteArray?, ByteArray?> {
        val template = RedisTemplate<ByteArray?, ByteArray?>()
        template.connectionFactory = redisConnectionFactory
        template.keySerializer = stringSerializer
        template.hashKeySerializer = stringSerializer
        template.valueSerializer = stringSerializer
        return template
    }

    @Bean(name = ["typeRedisTemplate"])
    fun typeRedisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = redisConnectionFactory

        val dtoMapper = objectMapper.copy().apply {
            registerKotlinModule()
            activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.PROPERTY
            )
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

        val jsonSerializer = GenericJackson2JsonRedisSerializer(dtoMapper)

        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = jsonSerializer
        template.hashValueSerializer = jsonSerializer
        template.hashValueSerializer = jsonSerializer

        return template
    }

    @Bean
    fun reactiveStringRedisTemplate(
        reactiveFactory: ReactiveRedisConnectionFactory
    ): ReactiveStringRedisTemplate {
        return ReactiveStringRedisTemplate(reactiveFactory)
    }

    @Bean
    fun cacheManager(
        @Qualifier("redisConnectionFactory")
        factory: RedisConnectionFactory
    ): CacheManager {
        val dtoMapper = objectMapper.copy().apply {
            registerKotlinModule()
            activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.PROPERTY
            )
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

        val productSerializer = GenericJackson2JsonRedisSerializer(dtoMapper)

        val config = RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer())
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(productSerializer)
            )
            .entryTtl(Duration.ofMinutes(60))
            .disableCachingNullValues()

        return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .transactionAware()
            .build()
    }


}
