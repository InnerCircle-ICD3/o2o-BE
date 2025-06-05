package com.eatngo.common.exception

import com.eatngo.common.error.BusinessErrorCode
import org.slf4j.event.Level

open class UserAccountException(
    override val errorCode: BusinessErrorCode,
    override val message: String = errorCode.message,
    override val data: Map<String, Any>? = null,
    override val logLevel: Level = Level.WARN,
) : BusinessException(errorCode, message, data, logLevel) {

    class UserAccountNotfoundException(userAccountId: Long) : UserAccountException(
        errorCode = BusinessErrorCode.USER_NOT_FOUND,
        message = "${BusinessErrorCode.USER_NOT_FOUND.message} (ID: $userAccountId)",
        data = mapOf("userAccountId" to userAccountId)
    )

}