package com.eatngo.search.service

import com.eatngo.common.exception.search.SearchException
import com.eatngo.common.type.CoordinateVO
import com.eatngo.common.util.DistanceCalculator
import com.eatngo.common.util.customNormalizeFloor
import com.eatngo.extension.orThrow
import com.eatngo.search.constant.SuggestionType
import com.eatngo.search.domain.SearchStore
import com.eatngo.search.dto.Box
import com.eatngo.search.dto.SearchStoreMap
import com.eatngo.search.dto.SearchStoreMapResultDto
import com.eatngo.search.dto.SearchStoreResultDto
import com.eatngo.search.dto.SearchSuggestionDto
import com.eatngo.search.dto.SearchSuggestionResultDto
import com.eatngo.search.dto.StoreFilterDto
import com.eatngo.search.infra.SearchMapRedisRepository
import com.eatngo.search.infra.SearchStoreRepository
import com.eatngo.search.infra.SearchSuggestionRedisRepository
import com.eatngo.search.infra.SearchSuggestionRepository
import com.eatngo.store.service.StoreTotalStockService
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class SearchService(
    private val searchStoreRepository: SearchStoreRepository,
    private val searchSuggestionRepository: SearchSuggestionRepository,
    private val searchMapRedisRepository: SearchMapRedisRepository,
    private val searchSuggestionRedisRepository: SearchSuggestionRedisRepository,
    private val storeTotalStockService: StoreTotalStockService,
) {
    val cacheBoxSize = 0.01

    /**
     * 가게 검색 API - 검색어 입력
     * @param StoreFilterDto 검색하는 유저의 위치 정보와 검색어
     * @param size 페이지 사이즈
     * @return 검색 결과 DTO
     */
    fun searchStore(
        storeFilterDto: StoreFilterDto,
        searchDistance: Double,
        size: Int,
    ): SearchStoreResultDto {
        val cachedFirstPageList =
            getFirstPageFromRedis(
                userCoordinate = storeFilterDto.viewCoordinate,
            )

        val searchStoreList =
            if (isFirstPage(storeFilterDto)) {
                cachedFirstPageList
            } else {
                val cachedStoreIds = cachedFirstPageList.map { it.storeId }.toSet()
                getFilteredSearchStoreList(storeFilterDto, searchDistance, size, cachedStoreIds)
            }

        return buildSearchResult(
            userCoordinate = storeFilterDto.viewCoordinate,
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

        val searchStoreList: List<SearchStore> = getMapListFromBox(box)

        return SearchStoreMapResultDto.from(
            box = box,
            searchStoreMapList = searchStoreList.map { SearchStoreMap.from(it) }, // SearchStore를 SearchStoreMap으로 변환,
        )
    }

    /**
     * 지도 검색 refresh API - MongoDB에서 검색하여 가져온 뒤 캐싱한다
     * @param userCoordinate 검색하는 사용자의 위치 정보
     * @return 지도 검색 결과 DTO
     */
    fun searchStoreMapRefresh(userCoordinate: CoordinateVO): SearchStoreMapResultDto {
        // 검색 결과가 없으면 MongoDB에서 검색하여 가져온 뒤 캐싱한다
        val box: Box =
            getBox(
                longitude = userCoordinate.longitude,
                latitude = userCoordinate.latitude,
            )
        val searchMapList = getMapListDBAndSaveCache(box)
        return SearchStoreMapResultDto.from(
            box = box,
            searchStoreMapList = searchMapList.map { SearchStoreMap.from(it) }, // SearchStore를 SearchStoreMap으로 변환
        )
    }

    /**
     * 검색어 자동완성 API
     * @param keyword 검색어
     * @return 검색어 자동완성 리스트
     */
    fun searchSuggestions(keyword: String): SearchSuggestionResultDto {
        // Redis에서 검색어 자동완성 결과를 가져온다. TODO: 로직 개선
        val redisKey = searchSuggestionRedisRepository.getKey(keyword)
        var cachedSuggestions: List<SearchSuggestionDto> =
            searchSuggestionRedisRepository.getSuggestsByKey(redisKey)

        if (cachedSuggestions.isEmpty()) {
            // Redis에 캐싱된 검색어 자동완성 결과가 없으면 MongoDB에서 검색하여 가져온다
            cachedSuggestions = getSuggestionFromMongo(keyword)

            // MongoDB에서 가져온 검색어 자동완성 결과를 Redis에 저장한다
            searchSuggestionRedisRepository.saveSuggests(
                key = redisKey,
                suggests = cachedSuggestions,
            )
        }

        return SearchSuggestionResultDto.from(
            storeNameList =
                cachedSuggestions
                    .filter { it.field == SuggestionType.STORE_NAME }
                    .sortedBy { it.value },
            foodList =
                cachedSuggestions
                    .filter { it.field == SuggestionType.FOOD_TYPE }
                    .sortedBy { it.value },
        )
    }

    /**
     * 검색 쿼리에서 box 좌표(0.01단위로 캐싱)를 구하는 메서드
     * @param longitude 경도
     * @param latitude 위도
     * @return Box 객체
     */
    fun getBox(
        longitude: Double,
        latitude: Double,
    ): Box {
        val unit = BigDecimal.valueOf(cacheBoxSize)

        val lngBD = BigDecimal.valueOf(longitude)
        val latBD = BigDecimal.valueOf(latitude)

        val leftLng = lngBD.customNormalizeFloor(unit).setScale(2, RoundingMode.HALF_UP).toDouble()
        val rightLng =
            BigDecimal
                .valueOf(leftLng)
                .add(unit)
                .setScale(2, RoundingMode.HALF_UP)
                .toDouble()
        val bottomLat = latBD.customNormalizeFloor(unit).setScale(2, RoundingMode.HALF_UP).toDouble()
        val topLat =
            BigDecimal
                .valueOf(bottomLat)
                .add(unit)
                .setScale(2, RoundingMode.HALF_UP)
                .toDouble()

        val topLeft = CoordinateVO.from(longitude = leftLng, latitude = topLat)
        val bottomRight = CoordinateVO.from(longitude = rightLng, latitude = bottomLat)

        return Box.from(topLeft, bottomRight)
    }

    fun saveBoxRedis(box: Box): List<SearchStore> {
        val redisKey =
            searchMapRedisRepository.getKey(box.topLeft)

        val searchStoreList: List<SearchStore> = searchStoreRepository.findBox(box)
        // Redis에 삭제 후 다시 쓰기
        searchMapRedisRepository.deleteByKey(redisKey)
        searchMapRedisRepository
            .save(
                key = redisKey,
                value = searchStoreList,
            )

        return searchStoreList
    }

    fun getSuggestionFromMongo(keyword: String): List<SearchSuggestionDto> {
        // TODO: 로직 개선
        val storeNameList: List<SearchSuggestionDto> =
            searchSuggestionRepository
                .getSuggestionsByKeyword(
                    keyword = keyword,
                    type = SuggestionType.STORE_NAME,
                    size = 5,
                ).map { it.to() }

        val foodCategoryList: List<SearchSuggestionDto> =
            searchSuggestionRepository
                .getSuggestionsByKeyword(
                    keyword = keyword,
                    type = SuggestionType.FOOD_TYPE,
                    size = 10,
                ).map { it.to() }

        return storeNameList + foodCategoryList
    }

    fun getMapListFromBox(box: Box): List<SearchStore> {
        val result = mutableListOf<SearchStore>()
        val topLeftList: List<CoordinateVO> =
            getNineBoxesTopLeftFromCenter(box.topLeft)

        topLeftList.forEach { topLeft ->
            // 좌상단 좌표를 기준으로 Redis에서 검색
            result.addAll(
                searchMapRedisRepository
                    .findByKey(searchMapRedisRepository.getKey(topLeft)),
            )
        }
        return result
    }

    fun getMapListDBAndSaveCache(box: Box): List<SearchStore> {
        val result = mutableListOf<SearchStore>()
        val topLeftList: List<CoordinateVO> =
            getNineBoxesTopLeftFromCenter(box.topLeft)

        topLeftList.forEach { topLeft ->
            val box =
                getBox(
                    longitude = topLeft.longitude,
                    latitude = topLeft.latitude,
                )
            // Redis에 저장
            result.addAll(saveBoxRedis(box))
        }

        return result
    }

    fun getNineBoxesTopLeftFromCenter(center: CoordinateVO): List<CoordinateVO> {
        val unit = BigDecimal.valueOf(cacheBoxSize)
        val centerLng = BigDecimal.valueOf(center.longitude)
        val centerLat = BigDecimal.valueOf(center.latitude)

        val topLeftList = mutableListOf<CoordinateVO>()
        for (i in -1..1) {
            for (j in -1..1) {
                val lng = centerLng.add(unit.multiply(BigDecimal.valueOf(i.toLong()))).toDouble()
                val lat = centerLat.add(unit.multiply(BigDecimal.valueOf(j.toLong()))).toDouble()

                val box = getBox(longitude = lng, latitude = lat)
                topLeftList.add(box.topLeft)
            }
        }
        return topLeftList
    }

    fun getFirstPageFromRedis(
        userCoordinate: CoordinateVO,
        size: Int = 5,
    ): List<SearchStore> {
        // center 값을 기준으로 해당하는 box 좌표를 구한다.
        val box: Box =
            getBox(
                longitude = userCoordinate.longitude,
                latitude = userCoordinate.latitude,
            )
        val searchStoreList: List<SearchStore> = getMapListFromBox(box)

        return searchStoreList
            .onEach { it.paginationToken = "FIRST_PAGE" }
            .sortedBy {
                DistanceCalculator.calculateDistance(
                    userCoordinate,
                    it.coordinate.toVO(),
                )
            }.take(size)
    }

    fun getFilteredSearchStoreList(
        storeFilterDto: StoreFilterDto,
        searchDistance: Double,
        size: Int,
        excludedStoreIds: Set<Long>,
    ): List<SearchStore> {
        val result =
            searchStoreRepository
                .searchStore(
                    longitude = storeFilterDto.viewCoordinate.longitude,
                    latitude = storeFilterDto.viewCoordinate.latitude,
                    maxDistance = searchDistance,
                    searchFilter = storeFilterDto.filter,
                    size = size,
                ).orThrow { SearchException.SearchStoreListFailed(storeFilterDto.viewCoordinate, storeFilterDto.filter) }
        return if (storeFilterDto.filter.searchText == null &&
            storeFilterDto.filter.storeCategory == null &&
            storeFilterDto.filter.time == null &&
            !storeFilterDto.filter.onlyReservable
        ) {
            result.filterNot { excludedStoreIds.contains(it.storeId) }
        } else {
            result
        }
    }

    fun isFirstPage(storeFilterDto: StoreFilterDto): Boolean =
        storeFilterDto.filter.searchText == null &&
            storeFilterDto.filter.storeCategory == null &&
            storeFilterDto.filter.time == null &&
            !storeFilterDto.filter.onlyReservable &&
            storeFilterDto.filter.lastId == null

    private fun buildSearchResult(
        userCoordinate: CoordinateVO,
        searchStoreList: List<SearchStore>,
    ): SearchStoreResultDto {
        val storeTotalStockMap =
            storeTotalStockService.getStoreStockMapForResponse(
                storeIds = searchStoreList.map { it.storeId },
            )

        return SearchStoreResultDto.from(
            userCoordinate = userCoordinate,
            totalStockCountMap = storeTotalStockMap,
            searchStoreList = searchStoreList,
        )
    }
}
