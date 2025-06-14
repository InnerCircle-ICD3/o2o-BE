package com.eatngo.notification

import com.eatngo.notification.service.NotificationSseService
import org.springframework.stereotype.Component

@Component
class NotificationBatch(
    private val notificationSseService: NotificationSseService,
)
