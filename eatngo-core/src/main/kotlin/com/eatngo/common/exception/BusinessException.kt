package com.eatngo.common.exception

import com.eatngo.common.error.ErrorCode

open class BusinessException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.message,
    val data: Map<String, Any>? = null,
    override val cause: Throwable? = null
) : RuntimeException(message, cause) {

    companion object {
        fun of(errorCode: ErrorCode): BusinessException =
            BusinessException(errorCode)

        fun of(errorCode: ErrorCode, message: String): BusinessException =
            BusinessException(errorCode, message)

        fun of(errorCode: ErrorCode, message: String, data: Map<String, Any>): BusinessException =
            BusinessException(errorCode, message, data = data)
    }
}