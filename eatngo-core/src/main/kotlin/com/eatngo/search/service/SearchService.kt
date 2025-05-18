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
    var cacheBoxSize = 0.005

    /**
     * 가게 검색 API
     * TODO: 검색 결과 캐싱 / page-size 방색에서 sort-offset 등 개선된 방식으로 변경
     * @param searchQuery 검색 쿼리
     * @param page 페이지 번호
     * @param size 페이지 사이즈
     * @return 검색 결과 DTO
     */
    fun searchStore(searchQuery: SearchStoreQueryDto, page: Int, size: Int): SearchStoreResultDto {
        val searchDistance = 2000.0 // 2km 검색 반경

        val searchStoreList: List<SearchStore> = searchStoreRepository.searchStore(
            lng = searchQuery.viewPoint.lng,
            lat = searchQuery.viewPoint.lat,
            maxDistance = searchDistance,
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

    /**
     * 지도 검색 API
     * @param searchQuery 검색 쿼리
     * @return 지도 검색 결과 DTO
     */
    fun searchStoreMap(searchQuery: SearchStoreQueryDto): SearchStoreMapResultDto {
        // center 값을 기준으로 해당하는 box 좌표를 구한다.
        val box: Box = getBox(
            lat = searchQuery.viewPoint.lat,
            lng = searchQuery.viewPoint.lng
        )

        // Redis에서 box 검색 결과를 가져온다. -> 위경도 기중 0.005 단위로 박스 매핑
        val redisKey = searchMapRedisRepository.getKey(box.topLeft)
        val seachStoreMapList: List<SearchStoreMap> = searchMapRedisRepository.findByKey(redisKey)

        // 검색 결과가 없으면 MongoDB에서 검색하여 가져온 뒤 캐싱한다
        if (seachStoreMapList.isEmpty()) {
            val searchStoreList: List<SearchStore> = searchStoreRepository.findBox(box)

            // Redis에 저장
            searchMapRedisRepository.save(
                key = redisKey,
                value = searchStoreList.map {
                    SearchStoreMap.from(it)
                }
            )
        }

        return SearchStoreMapResultDto(
            box = box,
            storeList = seachStoreMapList
        )
    }

    /**
     * 검색어 자동완성 API
     * TODO : 로직 전체 점검, 자동완성 범위(매장명, 카테고리 등) 점검
     * @param keyword 검색어
     * @return 검색어 자동완성 리스트
     */
    fun searchSuggestions(keyword: String): List<String> {
        val searchRecommendList: List<String> = searchStoreRepository.searchStoreRecommend(
            keyword = keyword
        )
        return searchRecommendList
    }

    fun getBox(lat: Double, lng: Double): Box {
        val topLeft = Point(
            lat = lat + cacheBoxSize,
            lng = lng - cacheBoxSize
        )
        val bottomRight = Point(
            lat = lat - cacheBoxSize,
            lng = lng + cacheBoxSize
        )
        return Box(topLeft, bottomRight)
    }
}