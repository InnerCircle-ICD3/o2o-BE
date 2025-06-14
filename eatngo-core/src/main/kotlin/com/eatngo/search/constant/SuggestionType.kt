package com.eatngo.search.constant

enum class SuggestionType(
    val code: Int,
) {
    STORE_NAME(1), // 매장 이름
    FOOD_TYPE(2), // 음식 종류
    ;

    companion object {
        private val codeMap = entries.associateBy { it.code }

        fun fromCode(code: Int): SuggestionType? = codeMap[code]
    }
}
