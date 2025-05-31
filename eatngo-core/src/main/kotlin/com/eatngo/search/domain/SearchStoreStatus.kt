package com.eatngo.search.domain

import com.eatngo.common.constant.StoreEnum

enum class SearchStoreStatus(
    val code: Int, // 상태 코드
) {
    PENDING(0),
    OPEN(1),
    CLOSED(2),
    ;

    companion object {
        fun from(status: StoreEnum.StoreStatus): SearchStoreStatus =
            when (status) {
                StoreEnum.StoreStatus.PENDING -> PENDING
                StoreEnum.StoreStatus.OPEN -> OPEN
                StoreEnum.StoreStatus.CLOSED -> CLOSED
            }

        fun from(code: Int): SearchStoreStatus =
            when (code) {
                0 -> PENDING
                1 -> OPEN
                2 -> CLOSED
                else -> throw IllegalArgumentException("Invalid status code: $code")
            }
    }

    fun toStoreStatus(): StoreEnum.StoreStatus =
        when (this) {
            PENDING -> StoreEnum.StoreStatus.PENDING
            OPEN -> StoreEnum.StoreStatus.OPEN
            CLOSED -> StoreEnum.StoreStatus.CLOSED
        }
}
