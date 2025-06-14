package com.eatngo.config

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.config.MeterFilter
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MetricsConfig {
    @Bean
    fun metricsCommonTags(): MeterRegistryCustomizer<MeterRegistry> {
        return MeterRegistryCustomizer { registry ->
            registry.config().commonTags(
                "application", "eatngo-store-owner",
                "service", "store-owner-api",
                "environment", "production"
            )
        }
    }

    @Bean
    fun storeOwnerMetricsFilter(): MeterFilter {
        return MeterFilter.denyNameStartsWith("customer")
    }
}