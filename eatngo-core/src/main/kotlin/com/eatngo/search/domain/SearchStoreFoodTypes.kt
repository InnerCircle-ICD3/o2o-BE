package com.eatngo.search.domain

/**
 * FoodTypes 업데이트용
 */
class SearchStoreFoodTypes(
    val storeId: Long,
    val foodTypes: List<String>,
) {
    companion object {
        fun from(
            storeId: Long,
            foodTypes: List<String>,
        ): SearchStoreFoodTypes = SearchStoreFoodTypes(storeId, foodTypes)

        fun of(
            storeId: Long,
            foodTypes: List<String>,
        ): SearchStoreFoodTypes = SearchStoreFoodTypes(storeId, foodTypes)
    }
}
