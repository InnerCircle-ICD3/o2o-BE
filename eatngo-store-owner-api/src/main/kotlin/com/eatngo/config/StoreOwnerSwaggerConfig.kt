package com.eatngo.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.servers.Server
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
    servers = [
        Server(
            url = "https://store-owner.eatngo.org",
            description = "Production server",
        ),
        Server(
            url = "http://localhost:8080",
            description = "Local development server",
        ),
    ],
)
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