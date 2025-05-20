package com.eatngo.product.domain

class FoodTypes(
    val foods: List<Food>
) {
    companion object {
        fun create(foods: List<String>): FoodTypes {
            validateFoodTypes(foods)
            return FoodTypes(foods.map { Food(it) })
        }

        private fun validateFoodTypes(foods: List<String>) {
            require(foods.isNotEmpty()) { "음식 목록이 비어있을 수 없습니다." }
            val uniqueFoods = foods.toSet()
            require(uniqueFoods.size == foods.size) { "중복된 음식 이름이 있습니다." }
        }
    }
}