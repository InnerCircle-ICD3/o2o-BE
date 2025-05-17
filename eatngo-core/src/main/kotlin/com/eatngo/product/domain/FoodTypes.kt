package com.eatngo.product.domain

class FoodTypes(
    val foods: List<Food>
) {
    companion object {
        fun create(foods: List<String>): FoodTypes {
            require(foods.isNotEmpty()) { "음식 목록이 비어있을 수 없습니다." }
            return FoodTypes(foods.map { Food(it) })
        }
    }
}