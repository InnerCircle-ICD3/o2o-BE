package com.eatngo.common.response

sealed class ApiResponse<T> {
    abstract val success: Boolean

    data class Success<T>(
        override val success: Boolean = true,
        val data: T,
    ) : ApiResponse<T>()

    data class Error<T>(
        override val success: Boolean = false,
        val errorCode: String,
        val errorMessage: String,
        val data: Map<String, Any>? = null,
    ) : ApiResponse<T>()

    companion object {
        fun <T> success(data: T): ApiResponse<T> = Success(data = data)

        fun <T> error(
            errorCode: String,
            errorMessage: String,
            data: Map<String, Any>? = null,
        ): ApiResponse<T> = Error(errorCode = errorCode, errorMessage = errorMessage, data = data)
    }
}
