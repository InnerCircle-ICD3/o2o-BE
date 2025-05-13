package com.eatngo.common.exception

open class DuplicateException(
    val value: String,
    errorCode: ErrorCode = ErrorCode.DUPLICATE_ENTITY,
    message: String = "$value 값이 이미 존재합니다.",
    data: Map<String, Any>? = mapOf("duplicateValue" to value),
    cause: Throwable? = null
) : BusinessException(errorCode, message, data, cause) 