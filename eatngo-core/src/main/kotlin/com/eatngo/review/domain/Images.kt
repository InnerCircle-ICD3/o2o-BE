package com.eatngo.review.domain

data class Images(
    val images: List<String>,
) {
    init {
        require(images.size >= MIN_IMAGE_COUNT) { "이미지의 개수는 $MIN_IMAGE_COUNT 장 이상이여야 합니다." }
        require(images.size <= MAX_IMAGE_COUNT) { "이미지의 개수는 $MAX_IMAGE_COUNT 장을 넘어선 안됩니다." }
    }

    companion object {
        private const val MAX_IMAGE_COUNT = 3
        private const val MIN_IMAGE_COUNT = 0
    }
}
