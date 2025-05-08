package com.eatngo.storeowner

import com.eatngo.common.config.SwaggerConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@Import(SwaggerConfig::class)
@SpringBootApplication
class StoreOwnerApplication

fun main(args: Array<String>) {
    runApplication<StoreOwnerApplication>(*args)
}