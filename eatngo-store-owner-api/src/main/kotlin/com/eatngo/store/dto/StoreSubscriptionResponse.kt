package com.eatngo.store.dto
import java.time.LocalDateTime


/**
 * 매장을 구독하고 있는 사용자 정보를 간단하게 반환하는 점주용 리스폰스
 */
data class SubscriptionResponseForStoreOwner(
    val id: Long,                    // 구독 ID
    val storeId: Long,               // 매장 ID
    val userId: String,              // 구독한 사용자의 계정 ID
    val userName: String,            // 구독한 사용자의 이름
    val subscribed: Boolean,         // 구독 여부 (토글 결과)
    val actionTime: LocalDateTime    // 구독/해제 시간
)