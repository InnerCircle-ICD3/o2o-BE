package com.eatngo

import com.eatngo.swagger.SwaggerConfig
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableScheduling

@Import(SwaggerConfig::class)
@EnableScheduling
@EnableFeignClients(basePackages = ["com.eatngo.oauth2.client"])
@OpenAPIDefinition(
    info = Info(title = "EatNGo 점주 API", version = "v1.0.0"),
    servers = [Server(url = "http://localhost:8081")]
)
@SpringBootApplication
class StoreOwnerApplication

fun main(args: Array<String>) {
    runApplication<StoreOwnerApplication>(*args)
}