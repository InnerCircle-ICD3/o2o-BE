package com.eatngo.swagger
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
    servers = [
        Server(
            url = "https://customer.eatngo.org",
            description = "Production server",
        ),
        Server(
            url = "http://localhost:8080",
            description = "Local development server",
        ),
    ],
)
@Configuration
class OpenApiConfig
