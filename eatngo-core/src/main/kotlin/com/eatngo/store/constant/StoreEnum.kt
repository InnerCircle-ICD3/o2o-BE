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
        PENDING     // 승인 대기
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
