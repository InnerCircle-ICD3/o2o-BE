package com.eatngo.common.exception

import com.eatngo.common.error.CommonErrorCode

open class FileException(
    val errorCode: CommonErrorCode,
    override val message: String = errorCode.message,
    val data: Map<String, Any>? = null
) : RuntimeException(message) {
    // 이미지 생성 실패
    class ImageUrlResolveFailed(imageUrl: String) : FileException(
        CommonErrorCode.IMAGE_URL_RESOLVE_FAILED,
        "${CommonErrorCode.IMAGE_URL_RESOLVE_FAILED.message} (imageUrl: $imageUrl)",
        mapOf("imageUrl" to imageUrl)
    )

}