package com.eatngo.subscription.controller

import com.eatngo.auth.annotaion.StoreOwnerId
import com.eatngo.subscription.notification.SubscriptionNotificationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/api/v1/notifications/subscriptions")
@Tag(name = "구독 알림 API", description = "구독 관련 실시간 알림 API")
class StoreOwnerSubscriptionNotificationController(
    private val notificationService: SubscriptionNotificationService
) {
    
    @Operation(summary = "구독 알림 스트림 연결", description = "점주가 구독 관련 실시간 알림을 받기 위한 SSE 스트림을 연결합니다.")
    @GetMapping(produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun subscribeToNotifications(@StoreOwnerId storeOwnerId: Long): SseEmitter {
        return notificationService.createStoreOwnerEmitter(storeOwnerId)
    }
} 