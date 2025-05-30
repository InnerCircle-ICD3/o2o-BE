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
        fun from(code: Int): SearchStoreStatus =
            when (code) {
                0 -> PENDING
                1 -> OPEN
                2 -> CLOSED
                else -> throw IllegalArgumentException("Invalid status code: $code")
            }

        fun from(storeStatus: StoreEnum.StoreStatus): SearchStoreStatus =
            when (storeStatus) {
                StoreEnum.StoreStatus.PENDING -> PENDING
                StoreEnum.StoreStatus.OPEN -> OPEN
                StoreEnum.StoreStatus.CLOSED -> CLOSED
            }
    }
}
