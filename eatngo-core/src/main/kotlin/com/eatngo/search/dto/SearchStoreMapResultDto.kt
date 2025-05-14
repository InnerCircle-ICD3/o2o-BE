package com.eatngo.search.dto

data class SearchStoreMapResultDto (
    val box: Box,
    val storeList: List<SearchStoreMap>,
)

data class SearchStoreMap (
    val storeID: Long,
    val storeName: String,
)

data class Box (
    val topLeft: Point,
    val bottomRight: Point,
)