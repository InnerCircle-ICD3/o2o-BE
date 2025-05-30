package com.eatngo.search.constant

// TODO: 추후 매장 도메인쪽 ENUM 가져오는 것으로 변경
object StoreEnum {
    enum class StoreCategory(
        val category: String,
    ) {
        // TODO: 우선 디자인 명세서에 구현된 값으로 정의
        BREAD("빵"),
        DESSERT("디저트"),
        KOREAN("한식"),
        FRUIT("과일"),
        PIZZA("피자"),
        SALAD("샐러드"),
        ;

        companion object {
            fun fromString(value: String?): StoreCategory? {
                if (value.isNullOrBlank()) return null

                return entries.find { it.name == value }
                    ?: entries.find { it.category == value }
                    ?: throw IllegalArgumentException("존재하지 않는 카테고리 입니다: $value")
            }
        }
    }
}
