package com.eatngo.common.constant

object StoreEnum {
    /**
     * 주소 타입
     */
    enum class AddressType {
        ROAD,   // 도로명 주소
        ADMIN,  // 행정동
        LEGAL   // 법정동
    }

    /**
     * 매장 상태
     */
    enum class StoreStatus {
        OPEN {
            override fun close(): StoreStatus = CLOSED
            override fun pending(): StoreStatus = PENDING
        },
        CLOSED {
            override fun open(): StoreStatus = OPEN
            override fun pending(): StoreStatus = PENDING
        },
        PENDING {
            override fun open(): StoreStatus = OPEN
            override fun close(): StoreStatus = CLOSED
        };

        open fun open(): StoreStatus = invalidTransition("open")
        open fun close(): StoreStatus = invalidTransition("close")
        open fun pending(): StoreStatus = invalidTransition("pending")

        fun soldOut(): StoreStatus = CLOSED

        private fun invalidTransition(action: String): StoreStatus =
            throw IllegalStateException("현재 상태($this)에서 '$action' 작업을 수행할 수 없습니다.")
    }

    enum class PickupDay {
        TODAY,   // 오늘 픽업
        TOMORROW // 내일 픽업
    }

    /**
     * 알림 유형
     */
    enum class NotificationType {
        NEW_PRODUCT,         // 새 상품 등록 알림
        DISCOUNT,            // 할인 알림
        RESERVATION_UPDATE,  // 예약 상태 변경 알림
        STORE_UPDATE         // 매장 정보 변경 알림
    }
}
