package com.eatngo.store.constant

object StoreEnum {
    /**
     * 주소 타입
     */
    enum class AddressType {
        ROAD,   // 도로명 주소
        LOT     // 지번 주소
    }

    /**
     * 매장 상태
     */
    enum class StoreStatus {
        OPEN,       // 영업 중
        CLOSED,     // 영업 종료
        VACATION,   // 휴업
        PENDING     // 승인 대기
    }
}
