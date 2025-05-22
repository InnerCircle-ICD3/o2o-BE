package com.eatngo.search.dto

enum class StoreStatus(
    val statusCode: Int
) {
    CLOSE(0),       // 영업종료
    OPEN(1),        // 영업중
    ALL(9);        // 전체 상태
}