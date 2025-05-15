package com.eatngo.search.service

import com.eatngo.search.dto.Address
import com.eatngo.search.dto.Box
import com.eatngo.common.type.Point
import com.eatngo.search.dto.SearchStore
import com.eatngo.search.dto.SearchStoreMap
import com.eatngo.search.dto.SearchStoreMapResultDto
import com.eatngo.search.dto.SearchStoreQueryDto
import com.eatngo.search.dto.SearchStoreResultDto
import org.springframework.stereotype.Service

@Service
class SearchService {

    fun searchStore(searchQuery: SearchStoreQueryDto, offset: Int): SearchStoreResultDto {
        // TODO: 리스트 검색 로직 구현
        val searchStore: SearchStore = SearchStore(
            storeID = 1L,
            storeName = "Test Store",
            storeCategory = listOf("한식", "중식"),
            foodCategory = listOf("햄버거", "피자"),
            distanceKm = 1.0,
            stock = 5,
            address = Address(
                address = "Test Address",
                point = Point(
                    lat = 36.456789,
                    lng = 127.012345
                )
            ),
        )

        return SearchStoreResultDto(
            storeList = listOf(searchStore)
        )
    }

    fun searchStoreMap(searchQuery: SearchStoreQueryDto): SearchStoreMapResultDto {
        val searchStore: SearchStoreMap = SearchStoreMap(
            storeID = 1L,
            storeName = "Test Store",
            stock = 5
        )
        val box: Box = Box(
            topLeft = Point(
                lat = 36.456789,
                lng = 127.012345
            ),
            bottomRight =  Point(
                lat = 36.456789,
                lng = 127.012345
            ),
        )

        return SearchStoreMapResultDto(
            box = box,
            storeList = listOf(searchStore)
        )
    }

    fun searchSuggestions(keyword: String): List<String> {
        return List(1) { "치킨" }
    }
}