package com.eatngo.search.service

import com.eatngo.common.exception.SearchException
import com.eatngo.common.type.CoordinateVO
import com.eatngo.extension.orThrow
import com.eatngo.search.domain.SearchStore
import com.eatngo.search.dto.Box
import com.eatngo.search.dto.SearchStoreMap
import com.eatngo.search.dto.SearchStoreMapResultDto
import com.eatngo.search.dto.SearchStoreQueryDto
import com.eatngo.search.dto.SearchStoreResultDto
import com.eatngo.search.infra.SearchMapRedisRepository
import com.eatngo.search.infra.SearchStoreRepository
import org.springframework.stereotype.Service
import kotlin.math.ceil
import kotlin.math.floor

@Service
class SearchService(
    private val searchStoreRepository: SearchStoreRepository,
    private val searchMapRedisRepository: SearchMapRedisRepository,
) {
    val cacheBoxSize = 0.005

    /**
     * 가게 검색 API
     * TODO: 검색 결과 캐싱 / page-size 방색에서 sort-offset 등 개선된 방식으로 변경
     * @param searchQuery 검색 쿼리
     * @param page 페이지 번호
     * @param size 페이지 사이즈
     * @return 검색 결과 DTO
     */
    fun searchStore(
        searchQuery: SearchStoreQueryDto,
        // TODO: 검색반경 프론트와 논의 필요
        searchDistance: Double = 2000.0,
        page: Int,
        size: Int,
    ): SearchStoreResultDto {
        val searchStoreList: List<SearchStore> =
            searchStoreRepository
                .searchStore(
                    longitude = searchQuery.viewCoordinate.longitude,
                    latitude = searchQuery.viewCoordinate.latitude,
                    maxDistance = searchDistance,
                    searchFilter = searchQuery.filter,
                    page = page,
                    size = size,
                ).orThrow { SearchException.SearchStoreListFailed(searchQuery) }

        return SearchStoreResultDto.from(
            userCoordinate = searchQuery.viewCoordinate,
            searchStoreList = searchStoreList,
        )
    }

    /**
     * 지도 검색 API
     * @param searchQuery 검색 쿼리
     * @return 지도 검색 결과 DTO
     */
    fun searchStoreMap(searchQuery: SearchStoreQueryDto): SearchStoreMapResultDto {
        // center 값을 기준으로 해당하는 box 좌표를 구한다.
        val box: Box =
            getBox(
                longitude = searchQuery.viewCoordinate.longitude,
                latitude = searchQuery.viewCoordinate.latitude,
            )

        // Redis에서 box 검색 결과를 가져온다. -> 위경도 기중 0.005 단위로 박스 매핑
        val redisKey = searchMapRedisRepository.getKey(box.topLeft)
        val seachStoreMapList: List<SearchStoreMap> = searchMapRedisRepository.findByKey(redisKey)

        // 검색 결과가 없으면 MongoDB에서 검색하여 가져온 뒤 캐싱한다
        if (seachStoreMapList.isEmpty()) {
            val searchStoreList: List<SearchStore> =
                searchStoreRepository.findBox(box).orThrow {
                    SearchException.SearchStoreMapFailed(searchQuery)
                }

            // Redis에 저장
            searchMapRedisRepository
                .save(
                    key = redisKey,
                    value =
                        searchStoreList.map {
                            SearchStoreMap.from(it)
                        },
                ).orThrow {
                    SearchException.SearchStoreMapCacheFailed(redisKey)
                }
        }

        return SearchStoreMapResultDto.from(
            box = box,
            searchStoreMapList = seachStoreMapList,
        )
    }

    /**
     * 검색어 자동완성 API
     * TODO : 로직 전체 점검, 자동완성 범위(매장명, 카테고리 등) 점검
     * @param keyword 검색어
     * @return 검색어 자동완성 리스트
     */
    fun searchSuggestions(keyword: String): List<String> {
        val searchRecommendList: List<String> =
            searchStoreRepository
                .searchStoreRecommend(
                    keyword = keyword,
                ).orThrow {
                    SearchException.SearchSuggestionFailed(keyword)
                }
        return searchRecommendList
    }

    /**
     * 검색 쿼리에서 box 좌표(0.005단위로 캐싱)를 구하는 메서드
     * @param longitude 경도
     * @param latitude 위도
     * @return Box 객체
     */
    fun getBox(
        longitude: Double,
        latitude: Double,
    ): Box {
        // 경도: 왼쪽(서쪽)으로 내림, 오른쪽(동쪽)으로 올림
        val leftLng = floor(longitude / cacheBoxSize) * cacheBoxSize
        val rightLng = ceil(longitude / cacheBoxSize) * cacheBoxSize

        // 위도: 위쪽(북쪽)으로 올림, 아래쪽(남쪽)으로 내림
        val topLat = ceil(latitude / cacheBoxSize) * cacheBoxSize
        val bottomLat = floor(latitude / cacheBoxSize) * cacheBoxSize

        val topLeft = CoordinateVO.from(longitude = leftLng, latitude = topLat) // 서쪽 + 북쪽
        val bottomRight = CoordinateVO.from(longitude = rightLng, latitude = bottomLat) // 동쪽 + 남쪽

        return Box.from(topLeft, bottomRight)
    }
}
