package com.eatngo.search.dto

import com.eatngo.search.domain.SearchStore

data class SearchStoreMapResultDto(
    val box: BoxResult,
    val storeList: List<SearchStoreMap>,
) {
    companion object {
        fun from(
            box: Box,
            searchStoreMapList: List<SearchStoreMap>,
        ): SearchStoreMapResultDto =
            SearchStoreMapResultDto(
                box = BoxResult.from(box),
                storeList = searchStoreMapList,
            )
    }
}

data class SearchStoreMap(
    val storeId: Long,
    val storeName: String,
    val coordinate: CoordinateResultDto, // 매장 위치(위도, 경도)
) {
    companion object {
        fun from(searchStore: SearchStore): SearchStoreMap =
            SearchStoreMap(
                storeId = searchStore.storeId,
                storeName = searchStore.storeName,
                coordinate = searchStore.coordinate.toDto(),
            )
    }
}

data class BoxResult(
    val topLeft: CoordinateResultDto,
    val bottomRight: CoordinateResultDto,
) {
    companion object {
        fun from(box: Box): BoxResult =
            BoxResult(
                topLeft = CoordinateResultDto.from(box.topLeft),
                bottomRight = CoordinateResultDto.from(box.bottomRight),
            )
    }
}
