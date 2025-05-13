package com.eatngo.common.response


sealed class ApiResponse<T> {
    abstract val success: Boolean
    
    data class Success<T>(
        override val success: Boolean = true,
        val data: T
    ) : ApiResponse<T>()
    
    data class Error<T>(
        override val success: Boolean = false,
        val errorCode: String,
        val errorMessage: String
    ) : ApiResponse<T>()
    
    companion object {
        fun <T> success(data: T): ApiResponse<T> {
            return Success(data = data)
        }

        fun <T> error(errorCode: String, errorMessage: String): ApiResponse<T> {
            return Error(errorCode = errorCode, errorMessage = errorMessage)
        }
    }
} 