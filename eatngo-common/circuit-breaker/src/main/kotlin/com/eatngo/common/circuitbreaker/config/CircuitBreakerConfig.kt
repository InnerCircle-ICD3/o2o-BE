package com.eatngo.common.circuitbreaker.config

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = ["com.eatngo.common.circuitbreaker"])
class CircuitBreakerConfig {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @Bean
    fun meterRegistry(): MeterRegistry {
        return SimpleMeterRegistry()
    }
} 