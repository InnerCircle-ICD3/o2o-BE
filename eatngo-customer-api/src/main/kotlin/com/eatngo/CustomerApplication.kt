package com.eatngo

import com.eatngo.swagger.SwaggerConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Import

@Import(SwaggerConfig::class)
@EnableFeignClients(basePackages = ["com.eatngo.oauth2.client"])
@SpringBootApplication
class CustomerApplication

fun main(args: Array<String>) {
    runApplication<CustomerApplication>(*args)
}