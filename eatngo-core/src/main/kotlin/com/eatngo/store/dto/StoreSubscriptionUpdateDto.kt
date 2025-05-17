package com.eatngo.store.dto

import com.eatngo.store.constant.StoreEnum
import com.eatngo.store.domain.Store
import com.eatngo.store.domain.StoreSubscription
import java.time.ZonedDateTime

/**
 * 상점 구독 알림 타입 설정 요청 DTO
 * 사용자가 구독한 매장에서 어떤 종류의 알림을 받을지 설정합니다.
 */
data class NotificationTypeUpdateRequest(
    val notificationTypes: List<StoreEnum.NotificationType>
)