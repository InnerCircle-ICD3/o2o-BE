package com.eatngo.configuration

import com.eatngo.resolver.TestCustomerIdArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class TestConfiguration(
    private val customerIdArgumentResolver: TestCustomerIdArgumentResolver
) : WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(customerIdArgumentResolver)
    }
}