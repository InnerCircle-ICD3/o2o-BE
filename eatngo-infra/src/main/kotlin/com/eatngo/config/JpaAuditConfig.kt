package com.eatngo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
class JpaAuditConfig {

    @Bean
    fun auditorProvider(auditorAware: AuditorAware<Long>): AuditorAware<Long> {
        return auditorAware
    }
}