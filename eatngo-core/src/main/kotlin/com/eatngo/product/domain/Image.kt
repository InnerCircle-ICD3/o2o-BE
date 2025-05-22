package com.eatngo.product.domain

data class Image(
    val fileName: String,
    val contentType: String,
    val folderPath: String?
) {
    init {
        if (fileName.isBlank()) {
            throw IllegalArgumentException("파일 이름은 비어있을 수 없습니다.")
        }

        if (contentType.isBlank()) {
            throw IllegalArgumentException("콘텐츠 타입은 비어있을 수 없습니다.")
        }
    }
}