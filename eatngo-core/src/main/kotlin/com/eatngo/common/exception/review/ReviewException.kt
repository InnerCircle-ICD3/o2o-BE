package com.eatngo.common.exception.review

import com.eatngo.common.error.BusinessErrorCode
import com.eatngo.common.exception.BusinessException
import org.slf4j.event.Level

open class ReviewException(
    override val errorCode: BusinessErrorCode,
    override val message: String = errorCode.message,
    override val data: Map<String, Any>? = null,
    override val logLevel: Level = Level.WARN,
) : BusinessException(errorCode, message, data, logLevel) {
    // 예외 생성
    class ReviewNotFoundExceptionByOrderId(
        orderId: Long,
    ) : ReviewException(
            errorCode = BusinessErrorCode.REVIEW_NOT_FOUND,
            message = "${BusinessErrorCode.REVIEW_NOT_FOUND.message} (ID: $orderId)",
            data = mapOf("orderId" to orderId),
        )

    class ReviewNotFoundException(
        reviewId: Long,
    ) : ReviewException(
            errorCode = BusinessErrorCode.REVIEW_NOT_FOUND,
            message = "${BusinessErrorCode.REVIEW_NOT_FOUND.message} (ID: $reviewId)",
            data = mapOf("reviewId" to reviewId),
        )
}
