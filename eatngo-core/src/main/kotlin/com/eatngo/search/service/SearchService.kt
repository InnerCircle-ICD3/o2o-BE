package com.eatngo.search.service

import com.eatngo.search.dto.Address
import com.eatngo.search.dto.Box
import com.eatngo.search.dto.Point
import com.eatngo.search.dto.SearchStore
import com.eatngo.search.dto.SearchStoreDto
import com.eatngo.search.dto.SearchStoreMap
import com.eatngo.search.dto.SearchStoreMapResultDto
import com.eatngo.search.dto.SearchStoreResultDto
import org.springframework.stereotype.Service

@Service
class SearchService {

    fun searchStore(searchDto: SearchStoreDto, offset: Int): SearchStoreResultDto {
        // TODO: 리스트 검색 로직 구현
        val searchStore: SearchStore = SearchStore(
            storeID = 1L,
            storeName = "Test Store",
            storeCategory = "Test Category",
            foodCategory = listOf("햄버거", "피자"),
            distanceKm = 1.0,
            openStatus = 1,
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

    fun searchStoreMap(searchDto: SearchStoreDto): SearchStoreMapResultDto {
        val searchStore: SearchStoreMap = SearchStoreMap(
            storeID = 1L,
            storeName = "Test Store",
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
}