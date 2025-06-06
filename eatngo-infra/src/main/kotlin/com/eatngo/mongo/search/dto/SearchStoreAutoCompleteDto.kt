package com.eatngo.mongo.search.dto

import org.springframework.data.annotation.Id

class SearchStoreAutoCompleteDto(
    @Id
    val storeId: Long,
    val storeName: String,
    val score: Double = 0.0,
)
