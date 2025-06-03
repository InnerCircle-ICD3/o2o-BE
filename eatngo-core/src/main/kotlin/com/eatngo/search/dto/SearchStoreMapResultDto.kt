package com.eatngo.search.dto

import com.eatngo.common.type.CoordinateVO
import com.eatngo.search.domain.SearchStore

data class SearchStoreMapResultDto(
    val box: Box,
    val storeList: List<SearchStoreMap>,
) {
    companion object {
        fun from(
            box: Box,
            searchStoreMapList: List<SearchStoreMap>,
        ): SearchStoreMapResultDto =
            SearchStoreMapResultDto(
                box = box,
                storeList = searchStoreMapList,
            )
    }
}

data class SearchStoreMap(
    val storeID: Long,
    val storeName: String,
    val coordinate: CoordinateResultDto, // 매장 위치(위도, 경도)
) {
    companion object {
        fun from(searchStore: SearchStore): SearchStoreMap =
            SearchStoreMap(
                storeID = searchStore.storeId,
                storeName = searchStore.storeName,
                coordinate = searchStore.coordinate.toDto(),
            )

        fun getMockSearchStoreMapList(): List<SearchStoreMap> =
            listOf(
                SearchStoreMap(storeID = 1L, storeName = "피자가게 이름", coordinate = CoordinateResultDto.from(37.566500, 126.978000)),
                SearchStoreMap(storeID = 2L, storeName = "피자나라 치킨공주", coordinate = CoordinateResultDto.from(37.567000, 126.979000)),
                SearchStoreMap(storeID = 3L, storeName = "치킨집", coordinate = CoordinateResultDto.from(37.568000, 126.980000)),
            )
    }
}

data class Box(
    val topLeft: CoordinateResultDto,
    val bottomRight: CoordinateResultDto,
) {
    companion object {
        fun from(
            topLeft: CoordinateVO,
            bottomRight: CoordinateVO,
        ): Box =
            Box(
                topLeft = CoordinateResultDto.from(topLeft),
                bottomRight = CoordinateResultDto.from(bottomRight),
            )
    }
}
