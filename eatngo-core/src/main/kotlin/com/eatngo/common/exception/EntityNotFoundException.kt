package com.eatngo.common.exception

open class EntityNotFoundException(
    val id: Any,
    val entityName: String,
    errorCode: ErrorCode = ErrorCode.ENTITY_NOT_FOUND,
    message: String = "$entityName(id: $id)를 찾을 수 없습니다.",
    data: Map<String, Any>? = mapOf("entityId" to id, "entityName" to entityName),
    cause: Throwable? = null
) : BusinessException(errorCode, message, data, cause) 