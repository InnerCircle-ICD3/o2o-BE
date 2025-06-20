package com.eatngo.common.exception

import com.eatngo.common.error.BusinessErrorCode
import org.slf4j.event.Level

abstract class BusinessException(
    open val errorCode: BusinessErrorCode,
    override val message: String = errorCode.message,
    open val data: Map<String, Any>? = null,
    open val logLevel: Level = Level.WARN,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)