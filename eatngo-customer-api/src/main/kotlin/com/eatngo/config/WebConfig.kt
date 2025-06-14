package com.eatngo.config

import com.eatngo.auth.resolver.CustomerIdArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val customerIdArgumentResolver: CustomerIdArgumentResolver
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(customerIdArgumentResolver)
    }
}