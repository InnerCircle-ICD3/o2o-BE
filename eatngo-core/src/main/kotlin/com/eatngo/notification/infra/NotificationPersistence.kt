package com.eatngo.notification.infra

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

interface NotificationPersistence {
    fun findOrCreate(storeId: Long): SseEmitter

    fun findById(storeId: Long): SseEmitter?
}