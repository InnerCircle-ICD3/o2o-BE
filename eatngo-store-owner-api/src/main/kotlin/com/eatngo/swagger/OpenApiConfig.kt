package com.eatngo.swagger

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
    servers = [
        Server(
            url = "https://store-owner.eatngo.org",
            description = "Production server",
        ),
    ],
)
@Configuration
class OpenApiConfig
