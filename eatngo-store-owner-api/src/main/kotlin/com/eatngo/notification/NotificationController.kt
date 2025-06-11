package com.eatngo.notification

import com.eatngo.auth.annotaion.StoreOwnerId
import com.eatngo.notification.service.NotificationSseService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Notification", description = "Notification 관련 API")
@RestController
class NotificationController(
    private val notificationSseService: NotificationSseService,
) {
    @GetMapping("/api/v1/stores/{storeId}/sse")
    @Operation(summary = "상점 주문 조회 SSE")
    fun getNotificationSse(
        @PathVariable storeId: Long,
        @StoreOwnerId storeOwnerId: Long,
    ) = ResponseEntity.ok(
        notificationSseService.findOrCreate(storeId),
    )
}
