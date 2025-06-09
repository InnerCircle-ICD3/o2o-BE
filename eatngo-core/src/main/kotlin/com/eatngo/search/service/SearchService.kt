package com.eatngo.search.service

import com.eatngo.common.exception.search.SearchException
import com.eatngo.common.type.CoordinateVO
import com.eatngo.extension.orThrow
import com.eatngo.search.domain.SearchStore
import com.eatngo.search.dto.AutoCompleteStoreNameDto
import com.eatngo.search.dto.Box
import com.eatngo.search.dto.SearchStoreMap
import com.eatngo.search.dto.SearchStoreMapResultDto
import com.eatngo.search.dto.SearchStoreResultDto
import com.eatngo.search.dto.SearchStoreWithDistance
import com.eatngo.search.dto.SearchSuggestionDto
import com.eatngo.search.dto.SearchSuggestionResultDto
import com.eatngo.search.dto.StoreFilterDto
import com.eatngo.search.dto.StoreSearchFilterDto
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
     * 매장 리스트 조회 API
     * @param storeFilterDto 검색 정보
     * @param page 페이지 번호
     * @param size 페이지 사이즈
     * @return 검색 결과 DTO
     */
    fun listStore(
        storeFilterDto: StoreFilterDto,
        searchDistance: Double,
        page: Int,
        size: Int,
    ): SearchStoreResultDto {
        val listStore: List<SearchStoreWithDistance> =
            searchStoreRepository
                .listStore(
                    longitude = storeFilterDto.viewCoordinate.longitude,
                    latitude = storeFilterDto.viewCoordinate.latitude,
                    maxDistance = searchDistance,
                    searchFilter = storeFilterDto.filter,
                    page = page,
                    size = size,
                ).orThrow { SearchException.SearchStoreListFailed(storeFilterDto.viewCoordinate, storeFilterDto.filter) }

        return SearchStoreResultDto.from(
            searchStoreList = listStore,
        )
    }

    /**
     * 가게 검색 API - 검색어 입력
     * @param storeSearchFilterDto 검색하는 유저의 위치 정보와 검색어
     * @param page 페이지 번호
     * @param size 페이지 사이즈
     * @return 검색 결과 DTO
     */
    fun searchStore(
        storeSearchFilterDto: StoreSearchFilterDto,
        searchDistance: Double,
        page: Int,
        size: Int,
    ): SearchStoreResultDto {
        val searchStoreList: List<SearchStore> =
            searchStoreRepository
                .searchStore(
                    longitude = storeSearchFilterDto.viewCoordinate.longitude,
                    latitude = storeSearchFilterDto.viewCoordinate.latitude,
                    maxDistance = searchDistance,
                    searchText = storeSearchFilterDto.searchText,
                    page = page,
                    size = size,
                ).orThrow { SearchException.SearchStoreSearchFailed(storeSearchFilterDto.viewCoordinate, storeSearchFilterDto.searchText) }

        return SearchStoreResultDto.from(
            userCoordinate = storeSearchFilterDto.viewCoordinate,
            searchStoreList = searchStoreList,
        )
    }

    /**
     * 지도 검색 API
     * @param userCoordinate 검색하는 사용자의 위치 정보
     * @return 지도 검색 결과 DTO
     */
    fun searchStoreMap(userCoordinate: CoordinateVO): SearchStoreMapResultDto {
        // center 값을 기준으로 해당하는 box 좌표를 구한다.
        val box: Box =
            getBox(
                longitude = userCoordinate.longitude,
                latitude = userCoordinate.latitude,
            )

        // Redis에서 box 검색 결과를 가져온다. -> 위경도 기중 0.005 단위로 박스 매핑
        val redisKey =
            searchMapRedisRepository.getKey(box.topLeft)
        val searchStoreMapList: List<SearchStoreMap> =
            searchMapRedisRepository.findByKey(redisKey).orThrow {
                SearchException.SearchStoreMapFailed(userCoordinate)
            }

        return SearchStoreMapResultDto.from(
            box = box,
            searchStoreMapList = searchStoreMapList,
        )
    }

    fun searchStoreMapRefresh(userCoordinate: CoordinateVO): SearchStoreMapResultDto {
        val searchStoreMapResult: SearchStoreMapResultDto = searchStoreMap(userCoordinate)
        if (searchStoreMapResult.storeList.isNotEmpty()) {
            // Redis에 캐싱된 검색 결과가 있으면 그대로 반환
            return searchStoreMapResult
        }

        // 검색 결과가 없으면 MongoDB에서 검색하여 가져온 뒤 캐싱한다
        val box: Box =
            getBox(
                longitude = userCoordinate.longitude,
                latitude = userCoordinate.latitude,
            )
        val searchMapList =
            saveBoxRedis(
                box = box,
            ).orThrow {
                SearchException.SearchStoreMapCacheFailed(userCoordinate)
            }

        return SearchStoreMapResultDto.from(
            box = box,
            searchStoreMapList = searchMapList,
        )
    }

    /**
     * 검색어 자동완성 API
     * @param keyword 검색어
     * @return 검색어 자동완성 리스트
     */
    fun searchSuggestions(keyword: String): SearchSuggestionResultDto {
        // 매장명은 autoComplete로 검색어 추천
        val storeNameList: List<AutoCompleteStoreNameDto> =
            searchStoreRepository
                .autocompleteStoreName(
                    keyword = keyword,
                )
        val storeSuggestionList =
            storeNameList.map {
                SearchSuggestionDto.from(
                    value = it.storeName,
                    field = "storeName",
                    storeId = it.storeId,
                )
            }
        // 음식명 추천은 ...
        val foodCategoryList: List<String> = emptyList() // TODO: 음식명 추천 로직 추가 필요
        val foodSuggestionList =
            foodCategoryList.map {
                SearchSuggestionDto.from(
                    value = it,
                    field = "foodCategory",
                )
            }

        return SearchSuggestionResultDto.from(
            storeNameList = storeSuggestionList,
            foodList = foodSuggestionList,
        )
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

    fun saveBoxRedis(box: Box): List<SearchStoreMap> {
        val redisKey =
            searchMapRedisRepository.getKey(box.topLeft)

        val searchStoreList: List<SearchStore> = searchStoreRepository.findBox(box)
        val searchStoreMap = searchStoreList.map { SearchStoreMap.from(it) }
        // Redis에 저장
        searchMapRedisRepository
            .save(
                key = redisKey,
                value = searchStoreMap,
            )

        return searchStoreMap
    }
}
