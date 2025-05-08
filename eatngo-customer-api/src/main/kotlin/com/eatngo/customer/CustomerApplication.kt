package com.eatngo.customer

import com.eatngo.common.config.SwaggerConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@Import(SwaggerConfig::class)
@SpringBootApplication
class CustomerApplication

fun main(args: Array<String>) {
    runApplication<CustomerApplication>(*args)
}