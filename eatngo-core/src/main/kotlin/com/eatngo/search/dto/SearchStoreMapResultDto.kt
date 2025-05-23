package com.eatngo.search.dto

import com.eatngo.common.type.Point
import com.eatngo.search.domain.SearchStore

data class SearchStoreMapResultDto (
    val box: Box,
    val storeList: List<SearchStoreMap>,
)

data class SearchStoreMap (
    val storeID: Long,
    val storeName: String,
    val location: Point, // 매장 위치(위도, 경도)
    val stock: Int=1, // 상품 재고 수량 TODO
) {
    companion object {
        fun from(searchStore: SearchStore): SearchStoreMap {
            return SearchStoreMap(
                storeID = searchStore.storeId,
                storeName = searchStore.storeName,
                location = searchStore.location
            )
        }
    }
}

data class Box (
    val topLeft: Point,
    val bottomRight: Point,
)