package com.eatngo.mongo.search.dto

import org.springframework.data.annotation.Id

class SearchStoreAutoCompleteDto(
    @Id
    val storeId: Long,
    val storeName: String,
) {
    companion object {
        fun from(
            storeId: Long,
            storeName: String,
        ): SearchStoreAutoCompleteDto =
            SearchStoreAutoCompleteDto(
                storeId = storeId,
                storeName = storeName,
            )
    }
}
