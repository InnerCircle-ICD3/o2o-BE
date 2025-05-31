package com.eatngo.search.dto

import com.eatngo.search.domain.SearchStore

data class SearchStoreWithDistance(
    val store: SearchStore,
    val distance: Double,
)
