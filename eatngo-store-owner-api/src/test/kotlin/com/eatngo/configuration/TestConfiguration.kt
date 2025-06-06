package com.eatngo.configuration

import com.eatngo.resolver.TestCustomerIdArgumentResolver
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.ProjectListener
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.testcontainers.containers.GenericContainer

@TestConfiguration
class TestConfiguration(
    private val customerIdArgumentResolver: TestCustomerIdArgumentResolver
) : WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(customerIdArgumentResolver)
    }
}



object RedisTestContainer : ProjectListener {

    private val container = GenericContainer<Nothing>("redis:7.2.4").apply {
        withExposedPorts(6379)
    }

    override suspend fun beforeProject() {
        container.start()
        System.setProperty("spring.data.redis.host", container.host)
        System.setProperty("spring.data.redis.port", container.getMappedPort(6379).toString())
    }

    override suspend fun afterProject() {
        container.stop()
    }
}


object ProjectConfig : AbstractProjectConfig() {
    override fun listeners() = listOf(RedisTestContainer)
}