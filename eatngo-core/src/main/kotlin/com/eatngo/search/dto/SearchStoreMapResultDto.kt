package com.eatngo.search.dto

data class SearchStoreMapResultDto (
    val box: Box,
    val storeList: List<SearchStoreMap>,
)

data class SearchStoreMap (
    val storeID: Long,
    val storeName: String,
    val stock: Int, // 상품 재고 수량
)

data class Box (
    val topLeft: Point,
    val bottomRight: Point,
)