package com.eatngo.common.response

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val errorCode: String? = null,
    val errorMessage: String? = null
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(
                success = true,
                data = data
            )
        }

        fun <T> error(errorCode: String, errorMessage: String): ApiResponse<T> {
            return ApiResponse(
                success = false,
                errorCode = errorCode,
                errorMessage = errorMessage
            )
        }
    }
} 