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

    /**
     * 가게 검색 API
     * TODO: 검색 결과 캐싱 / page-size 방색에서 sort-offset 등 개선된 방식으로 변경
     * @param searchQuery 검색 쿼리
     * @param page 페이지 번호
     * @param size 페이지 사이즈
     * @return 검색 결과 DTO
     */
    fun searchStore(searchQuery: SearchStoreQueryDto, page: Int, size: Int): SearchStoreResultDto {
        val searchStoreList: List<SearchStore> = searchStoreRepository.searchStore(
            lng = searchQuery.viewPoint.lng,
            lat = searchQuery.viewPoint.lat,
            maxDistance = 3000.0,
            searchFilter = searchQuery.filter,
            page = page,
            size = size
        )

        return SearchStoreResultDto(
            storeList = searchStoreList.map {
                SearchStoreDto.from(
                    userPoint = searchQuery.viewPoint,
                    searchStore = it
                )
            }
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
        val searchRecommendList: List<String> = searchStoreRepository.searchStoreRecommend(
            keyword = keyword
        )
        return searchRecommendList
    }
}