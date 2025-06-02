package com.eatngo.health

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "HealthCheck", description = "ALB HealthCheck 관련 API")
@RestController
@PreAuthorize("hasRole('ROLE_CUSTOMER')")
class HealthCheckController {
    @GetMapping("/health")
    fun healthCheck(): ResponseEntity<String> {
        return ResponseEntity.ok("OK")
    }
}