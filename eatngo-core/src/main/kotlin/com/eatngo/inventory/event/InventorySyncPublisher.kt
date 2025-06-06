package com.eatngo.inventory.event

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class InventorySyncPublisher(
    private val eventPublisher: ApplicationEventPublisher
) {

    fun publishEvent(event: InventorySyncEvent) {
        eventPublisher.publishEvent(event)
    }
}