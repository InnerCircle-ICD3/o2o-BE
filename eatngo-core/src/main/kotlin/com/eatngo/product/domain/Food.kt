package com.eatngo.product.domain

data class Food(
    val name: String
) {
    init {
        require(name.isNotBlank()) {"음식의 이름은 비어있을 수 없습니다."}
    }
}