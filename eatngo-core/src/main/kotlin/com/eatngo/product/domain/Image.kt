package com.eatngo.product.domain

data class Image(
    val fileName: String,
    val contentType: String,
    val folderPath: String?
) {
    init {
        require(fileName.isNotBlank()) { "파일 이름은 비어있을 수 없습니다." }
        require(contentType.isNotBlank()) { "콘텐츠 타입은 비어있을 수 없습니다." }
    }
}