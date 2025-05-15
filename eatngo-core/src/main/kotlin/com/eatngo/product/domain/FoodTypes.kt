package com.eatngo.product.domain

class FoodTypes(
    val foods: List<Food>
) {
    companion object {
        fun create(foods: List<String>): FoodTypes {
            return FoodTypes(foods.map { Food(it) })
        }
    }
}