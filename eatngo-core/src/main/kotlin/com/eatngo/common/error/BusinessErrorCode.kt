package com.eatngo.common.error

/**
 * 비즈니스 에러 코드 정의
 * 도메인 로직 처리 중 발생하는 오류에 대한 코드
 */
enum class BusinessErrorCode(
    override val code: String,
    override val message: String,
    override val httpStatus: Int? = null
) : ErrorCode {
    // 일반 비즈니스 오류
    BUSINESS_EXCEPTION("B001", "비즈니스 로직 처리 중 오류가 발생했습니다.", 500),

    // 사용자 관련 오류
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다.", 404),

    // 고객 관련 오류
    CUSTOMER_NOT_FOUND("C001", "고객을 찾을 수 없습니다.", 404),

    // 고객 주소 관련 오류
    CUSTOMER_ADDRESS_NOT_FOUND("CA001", "고객 주소를 찾을 수 없습니다.", 404),
    CUSTOMER_ADDRESS_ALREADY_EXISTS("CA002", "이미 등록된 고객 주소입니다.", 409),

    // 주문 관련 오류
    ORDER_NOT_FOUND("O001", "주문을 찾을 수 없습니다.", 404),
    ORDER_ALREADY_COMPLETED("O002", "이미 완료된 주문입니다.", 409),
    ORDER_ALREADY_CANCELED("O003", "이미 취소된 주문입니다.", 409),
    ORDER_ITEM_NOT_AVAILABLE("O004", "주문 가능하지 않은 메뉴입니다.", 400),

    // 주문 관련 오류
    REVIEW_NOT_FOUND("R001", "리뷰를 찾을 수 없습니다.", 404),

    // 주문 관리 오류
    ORDER_ACCEPT_FAILED("O005", "주문 수락에 실패했습니다.", 400),
    ORDER_REJECT_FAILED("O006", "주문 거절에 실패했습니다.", 400),
    ORDER_COMPLETE_FAILED("O007", "주문 완료 처리에 실패했습니다.", 400),
    ORDER_CANCEL_FAILED("O008", "주문 취소에 실패했습니다.", 400),
    ORDER_STATUS_INVALID("O009", "유효하지 않은 주문 상태입니다.", 400),

    // 결제 관련 오류 - 추후 사용
    PAYMENT_FAILED("P001", "결제에 실패했습니다.", 400),
    PAYMENT_ALREADY_PROCESSED("P002", "이미 처리된 결제입니다.", 409),
    PAYMENT_AMOUNT_MISMATCH("P003", "결제 금액이 일치하지 않습니다.", 400),

    // 매장 관련 오류
    STORE_NOT_FOUND("S001", "매장을 찾을 수 없습니다.", 404),
    STORE_CLOSED("S002", "영업 종료된 매장입니다.", 409),
    STORE_NOT_AVAILABLE("S003", "이용 불가능한 매장입니다.", 409),

    // 매장 구독 관련 오류
    SUBSCRIPTION_NOT_FOUND("S004", "구독 정보를 찾을 수 없습니다.", 404),
    SUBSCRIPTION_UPDATE_FAILED("S005", "구독 정보 업데이트에 실패했습니다.", 400),

    // 매장 생성 관련 오류
    STORE_VALIDATION_FAILED("S006", "매장 정보 검증에 실패했습니다.", 400),

    // 매장 점주 관련 오류
    STORE_OWNER_NOT_FOUND("S007", "점주 정보를 찾을 수 없습니다.", 404),
    STORE_OWNER_ALREADY_EXISTS("S008", "이미 등록된 점주입니다.", 409),
    STORE_REGISTRATION_FAILED("S009", "매장 등록에 실패했습니다.", 400),
    STORE_UPDATE_FAILED("S010", "매장 정보 수정에 실패했습니다.", 400),
    STORE_DELETE_FAILED("S011", "매장 삭제에 실패했습니다.", 400),

    // 매장 상태 관련 오류
    STORE_STATUS_INVALID("S012", "유효하지 않은 매장 상태입니다", 400),

    // 매장 권한 관련 오류
    STORE_OWNER_FORBIDDEN("S013", "해당 매장에 대한 권한이 없습니다.", 403),

    // 매장 구독 권한 관련 오류
    SUBSCRIPTION_FORBIDDEN("S014", "해당 구독 정보에 대한 권한이 없습니다.", 403),

    // 매장 구독 상태 관련 오류
    SUBSCRIPTION_ALREADY_ACTIVE("S015", "이미 활성화된 구독입니다.", 409),
    SUBSCRIPTION_ALREADY_CANCELED("S016", "이미 취소된 구독입니다.", 409),

    // 매장 스케줄러 관련 오류
    STORE_BATCH_UPDATE_FAILED("S017", "매장 상태 배치 업데이트에 실패했습니다.", 500),

    // 점주당 매장 개수 제한 오류
    STORE_OWNER_LIMIT_EXCEEDED("S018", "점주당 매장 등록 개수 제한을 초과했습니다.", 409),

    // 메뉴 관련 오류
    PRODUCT_NOT_FOUND("M001", "메뉴를 찾을 수 없습니다.", 404),
    PRODUCT_NOT_AVAILABLE("M002", "현재 제공하지 않는 메뉴입니다.", 409),
    PRODUCT_SOLD_OUT("M003", "품절된 메뉴입니다.", 409),
    PRODUCT_CREATION_FAILED("M005", "메뉴 등록에 실패했습니다.", 400),
    PRODUCT_UPDATE_FAILED("M006", "메뉴 수정에 실패했습니다.", 400),
    PRODUCT_DELETE_FAILED("M007", "메뉴 삭제에 실패했습니다.", 400),
    STORE_PRODUCT_COUNT_EXCEPTION("M002", "매장에 메뉴가 3개 초과일 수 없습니다.", 400),
    PRODUCT_TYPE_DUPLICATION_EXCEPTION("M002", "매장에 같은 종류의 메뉴가 있을 수 없습니다.", 400),

    // 검색 관련 오류
    SEARCH_INVALID_COORDINATE("H001", "유효하지 않은 좌표 입니다.", 400),
    SEARCH_INVALID_FILTER("H002", "유효하지 않은 검색 필터 입니다.", 400),
    SEARCH_STORE_LIST_FAILED("H003", "매장 목록 조회(필터링)에 실패했습니다.", 500),
    SEARCH_STORE_SEARCH_FAILED("H004", "매장 목록 조회(검색)에 실패했습니다.", 500),
    SEARCH_STORE_MAP_FAILED("H005", "매장 지도 조회에 실패했습니다.", 500),
    SEARCH_STORE_MAP_CACHE_FAILED("H006", "지도 정보 캐싱에 실패했습니다.", 500),
    SEARCH_SUGGESTION_FAILED("H007", "추천 검색어 조회에 실패했습니다.", 500),
    SEARCH_CATEGORY_NOT_FOUND("H008", "존재하지 않는 카테고리 입니다.", 400),

    // 재고 관련 오류
    STOCK_NOT_FOUND("ST001", "상품의 재고를 찾을 수 없습니다.", 400),
    STOCK_EMPTY("ST002", "상품의 재고가 없습니다.", 409),
    INVENTORY_NOT_FOUND("I001", "상품 재고를 찾을 수 없습니다.", 400),
    STORE_TOTAL_STOCK_QUERY_FAILED("ST003", "매장 총 재고 조회에 실패했습니다.", 500),
    STORE_CANNOT_OPEN_NO_STOCK("ST004", "재고가 없어 매장을 오픈할 수 없습니다.", 409),

}
