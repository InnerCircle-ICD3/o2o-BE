package com.eatngo.store.rdb.projection

import com.eatngo.common.constant.StoreEnum

/**
 * 스케줄러용 Store Projection
 */
interface StoreSchedulerProjection {
    val id: Long
    val businessHours: String // JSON 문자열
    val status: StoreEnum.StoreStatus
} 