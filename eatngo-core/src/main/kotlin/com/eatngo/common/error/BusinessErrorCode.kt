package com.eatngo.common.error

import com.eatngo.common.error.ErrorCode
import org.slf4j.event.Level
import org.springframework.http.HttpStatus

/**
 * 비즈니스 에러 코드 정의
 * 도메인 로직 처리 중 발생하는 오류에 대한 코드
 */
enum class BusinessErrorCode(
    override val code: String,
    override val message: String,
    override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    override val logLevel: Level = Level.WARN
) : ErrorCode {
    // 일반 비즈니스 오류
    BUSINESS_EXCEPTION("B001", "비즈니스 로직 처리 중 오류가 발생했습니다."),

    // 주문 관련 오류
    ORDER_NOT_FOUND("B101", "주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ORDER_ALREADY_COMPLETED("B102", "이미 완료된 주문입니다.", HttpStatus.CONFLICT),
    ORDER_ALREADY_CANCELED("B103", "이미 취소된 주문입니다.", HttpStatus.CONFLICT),
    ORDER_ITEM_NOT_AVAILABLE("B104", "주문 가능하지 않은 메뉴입니다."),

    // 결제 관련 오류 - 추후 사용
    PAYMENT_FAILED("B201", "결제에 실패했습니다."),
    PAYMENT_ALREADY_PROCESSED("B202", "이미 처리된 결제입니다.", HttpStatus.CONFLICT),
    PAYMENT_AMOUNT_MISMATCH("B203", "결제 금액이 일치하지 않습니다."),

    // 매장 관련 오류
    STORE_NOT_FOUND("B301", "매장을 찾을 수 없습니다.",  HttpStatus.NOT_FOUND),
    STORE_CLOSED("B302", "영업 종료된 매장입니다."),
    STORE_NOT_AVAILABLE("B303", "이용 불가능한 매장입니다."),

    // 메뉴 관련 오류
    MENU_NOT_FOUND("B401", "메뉴를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    MENU_NOT_AVAILABLE("B402", "현재 제공하지 않는 메뉴입니다."),
    MENU_SOLD_OUT("B403", "품절된 메뉴입니다.");
}