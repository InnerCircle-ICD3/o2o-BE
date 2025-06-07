package com.eatngo.auth.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry
            .addMapping("/**")
            .allowedOriginPatterns(
                "https://eatngo.org",
                "https://www.eatngo.org",
                "https://store-owner.eatngo.org",
                "https://customer.eatngo.org",
                "http://localhost:3000",
                "http://localhost:3001",
                "http://localhost:8080",
                "http://localhost:8081",
            ).allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true)
    }
}
