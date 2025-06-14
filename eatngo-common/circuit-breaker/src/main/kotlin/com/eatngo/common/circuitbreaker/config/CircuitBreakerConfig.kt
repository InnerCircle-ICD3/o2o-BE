package com.eatngo.common.circuitbreaker.config

import com.eatngo.common.circuitbreaker.metrics.CircuitBreakerMetrics
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = ["com.eatngo.common.circuitbreaker"])
class CircuitBreakerConfig(
    private val circuitBreakerMetrics: CircuitBreakerMetrics
) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @Bean
    fun meterRegistry(): MeterRegistry {
        return SimpleMeterRegistry()
    }
    
    @EventListener(ContextRefreshedEvent::class)
    fun initializeMetrics() {
        try {
            circuitBreakerMetrics.registerStateGauges()
            logger.info { "Circuit Breaker metrics initialized successfully" }
        } catch (ex: Exception) {
            logger.error(ex) { "Circuit Breaker metrics initialization failed: ${ex.message}" }
        }
    }
} 