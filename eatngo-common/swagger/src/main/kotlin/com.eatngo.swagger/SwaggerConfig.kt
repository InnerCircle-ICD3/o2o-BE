package com.eatngo.swagger

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@SecurityScheme(
    name = "securityCookie",
    type = SecuritySchemeType.APIKEY,
    `in` = SecuritySchemeIn.COOKIE,
    paramName = "access_token"
)
class SwaggerConfig(
    @Value("\${swagger.title}") private val title: String,
    @Value("\${swagger.description}") private val description: String,
    @Value("\${swagger.version}") private val version: String
) {
    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title(title)
                    .description(description)
                    .version(version)
            )
    }
} 