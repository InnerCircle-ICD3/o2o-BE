package com.eatngo.config

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.config.MeterFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MetricsConfig {
    @Value("\${spring.profiles.active:local}")
    private lateinit var activeProfile: String

    @Bean
    fun metricsCommonTags(): MeterRegistryCustomizer<MeterRegistry> {
        return MeterRegistryCustomizer { registry ->
            registry.config().commonTags(
                "application", "eatngo-customer",
                "service", "customer-api",
                "environment", activeProfile
            )
        }
    }

    @Bean
    fun customerMetricsFilter(): MeterFilter {
        return MeterFilter.denyNameStartsWith("store.owner")
    }
}