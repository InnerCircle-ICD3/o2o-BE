package com.eatngo.config

import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StoreOwnerSwaggerConfig {

    @Bean
    fun ignoreStoreOwnerIdParameter(): OpenApiCustomizer {
        return OpenApiCustomizer { openApi ->
            openApi.paths.values.forEach { pathItem ->
                pathItem.readOperations().forEach { operation ->
                    operation.parameters = operation.parameters
                        ?.filterNot { it.`in` == "query" && it.name == "storeOwnerId" }
                }
            }
        }
    }
}