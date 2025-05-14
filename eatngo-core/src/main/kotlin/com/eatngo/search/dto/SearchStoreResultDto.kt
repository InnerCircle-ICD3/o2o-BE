package com.eatngo.search.dto

data class SearchStoreResultDto (
    val storeList: List<SearchStore>,
)

data class SearchStore (
    val storeID: Long,
    // TODO: 좀 더 자세하게..
)
