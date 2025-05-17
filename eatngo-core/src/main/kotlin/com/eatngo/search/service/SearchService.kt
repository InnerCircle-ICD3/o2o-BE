package com.eatngo.search.service

import com.eatngo.search.dto.Box
import com.eatngo.common.type.Point
import com.eatngo.search.domain.SearchStore
import com.eatngo.search.dto.SearchStoreDto
import com.eatngo.search.dto.SearchStoreMap
import com.eatngo.search.dto.SearchStoreMapResultDto
import com.eatngo.search.dto.SearchStoreQueryDto
import com.eatngo.search.dto.SearchStoreResultDto
import com.eatngo.search.infra.SearchMapRedisRepository
import com.eatngo.search.infra.SearchStoreRepository
import org.springframework.stereotype.Service

@Service
class SearchService (
    private val searchStoreRepository: SearchStoreRepository,
    private val searchMapRedisRepository: SearchMapRedisRepository
) {

    fun searchStore(searchQuery: SearchStoreQueryDto, offset: Int): SearchStoreResultDto {
        // TODO: 리스트 검색 로직 구현
        val searchStore: SearchStore = SearchStore.create()

        return SearchStoreResultDto(
            storeList = listOf(
                SearchStoreDto.from(searchQuery.viewPoint, searchStore)
            )
        )
    }

    fun searchStoreMap(searchQuery: SearchStoreQueryDto): SearchStoreMapResultDto {
        val searchStoreList: List<SearchStore> = searchStoreRepository.findByLocation(
            lng = searchQuery.viewPoint.lng,
            lat = searchQuery.viewPoint.lat,
            maxDistance = 500.0
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
            storeList = searchStoreList.map {
                SearchStoreMap(
                    storeID = it.storeId,
                    storeName = it.storeName,
                    stock = 0
                )
            }
        )
    }

    fun searchSuggestions(keyword: String): List<String> {
        return List(1) { "치킨" }
    }
}