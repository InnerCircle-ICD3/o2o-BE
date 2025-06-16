package com.eatngo.notification

import com.eatngo.notification.service.NotificationSseService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class NotificationScheduler(
    private val notificationSseService: NotificationSseService,
) {
    private val logger = LoggerFactory.getLogger(NotificationScheduler::class.java)

    @Scheduled(cron = "0 * * * * *") // 매분 0초에 실행
    fun sendHeartBeat() {
        logger.info("Sending heartbeat")
        notificationSseService.sendHeartbeat()
        logger.info("Sending heartbeat end")
    }
}
