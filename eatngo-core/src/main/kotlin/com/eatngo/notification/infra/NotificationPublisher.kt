package com.eatngo.notification.infra

import com.eatngo.notification.event.NotificationEvent

interface NotificationPublisher {
    fun publish(notificationEvent: NotificationEvent<*>): Long
}