package com.eatngo.config

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CoroutineConfig {

    @Bean
    fun inventoryScope(): CoroutineScope = CoroutineScope(Dispatchers.IO)
}