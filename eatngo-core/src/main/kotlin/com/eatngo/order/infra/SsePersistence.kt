package com.eatngo.order.infra

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

interface SsePersistence {
    fun findOrCreate(storeId: Long): SseEmitter

    fun findById(storeId: Long): SseEmitter?
}