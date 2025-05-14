package com.eatngo.search.service

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

    fun searchStore(searchDto: SearchStoreDto): SearchStoreResultDto {
        val searchStore: SearchStore = SearchStore(
            storeID = 1L
        )

        return SearchStoreResultDto(
            storeList = listOf(searchStore)
        )
    }

    fun searchStoreMap(searchDto: SearchStoreDto): SearchStoreMapResultDto {
        val searchStore: SearchStoreMap = SearchStoreMap(
            storeID = 1L
        )
        val box: Box = Box(
            topLeft = Point(
                lat = "",
                lng = ""
            ),
            bottomRight =  Point(
                lat = "",
                lng = ""
            ),
        )

        return SearchStoreMapResultDto(
            box = box,
            storeList = listOf(searchStore)
        )
    }
}