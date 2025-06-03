package com.eatngo

import com.eatngo.swagger.SwaggerConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableScheduling

@Import(SwaggerConfig::class)
@EnableScheduling
@SpringBootApplication
class StoreOwnerApplication

fun main(args: Array<String>) {
    runApplication<StoreOwnerApplication>(*args)
}