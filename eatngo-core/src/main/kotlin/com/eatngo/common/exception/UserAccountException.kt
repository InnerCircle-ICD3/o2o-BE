package com.eatngo.common.exception

import com.eatngo.common.error.BusinessErrorCode

open class UserAccountException(
    val errorCode: BusinessErrorCode,
    override val message: String = errorCode.message,
    val data: Map<String, Any>? = null,
) : RuntimeException(message) {

    class UserAccountNotfoundException(userAccountId: Long) : UserAccountException(
        BusinessErrorCode.USER_NOT_FOUND,
        "${BusinessErrorCode.USER_NOT_FOUND.message} (ID: $userAccountId)",
        mapOf("userAccountId" to userAccountId)
    )

}