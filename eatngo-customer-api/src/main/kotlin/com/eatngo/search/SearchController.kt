package com.eatngo.search

import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.type.CoordinateVO
import com.eatngo.search.dto.SearchFilter
import com.eatngo.search.dto.SearchStoreMapResultDto
import com.eatngo.search.dto.SearchStoreResultDto
import com.eatngo.search.dto.SearchSuggestionResultDto
import com.eatngo.search.service.SearchService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Search", description = "검색 API")
@RestController
class SearchController(
    private val searchService: SearchService,
) {
    // TODO : 공통 Response로 묶기
    @Operation(
        summary = "매장 리스트 조회 API",
        description = "위치 기반 매장 리스트 조회 및 필터링 API",
    )
    @GetMapping("/api/v1/store/list")
    fun listStore(
        @RequestParam latitude: Double,
        @RequestParam longitude: Double,
        @RequestParam storeCategory: String?,
        @RequestParam time: String?, // HH:mm 형식의 시간 (ex: 12:30) TODO: VO로 정의하여 검증 로직 추가
        @RequestParam status: StoreEnum.StoreStatus?,
        // TODO : 우선 BE, FE 모두 page+size로 구현 => 추후 개선
        @RequestParam page: Int = 0,
        @RequestParam size: Int = 20,
    ): ResponseEntity<SearchStoreResultDto> =
        ResponseEntity.ok(
            searchService.listStore(
                CoordinateVO.from(
                    latitude = latitude,
                    longitude = longitude,
                ),
                SearchFilter.from(
                    storeCategory = storeCategory,
                    time = time,
                    status = status,
                ),
                page = page,
                size = size,
            ),
        )

    @Operation(summary = "가게 검색 API", description = "매장 텍스트 검색 API")
    @GetMapping("/api/v1/search/store")
    fun searchStoreKeyword(
        @RequestParam latitude: Double,
        @RequestParam longitude: Double,
        @RequestParam searchText: String,
        // TODO : 우선 BE, FE 모두 page+size로 구현 => 추후 개선
        @RequestParam page: Int = 0,
        @RequestParam size: Int = 20,
    ): ResponseEntity<SearchStoreResultDto> =
        ResponseEntity.ok(
            searchService.searchStore(
                userCoordinate =
                    CoordinateVO.from(
                        latitude = latitude,
                        longitude = longitude,
                    ),
                searchText = searchText,
                page = page,
                size = size,
            ),
        )

    @Operation(summary = "지도 검색 API", description = "지도에서 매장 포인트 리턴 API")
    @GetMapping("/api/v1/search/store/map")
    fun searchStoreMap(
        @RequestParam latitude: Double,
        @RequestParam longitude: Double,
    ): ResponseEntity<SearchStoreMapResultDto> =
        ResponseEntity.ok(
            searchService.searchStoreMap(
                CoordinateVO.from(
                    latitude = latitude,
                    longitude = longitude,
                ),
            ),
        )

    /**
     * 검색어 자동완성 API
     * @param keyword 검색어
     * @return 검색어 자동완성 리스트
     */
    @Operation(summary = "검색어 자동완성 API", description = "검색어 자동완성 API ex: '치킨' -> '치킨, 치킨너겟'")
    @GetMapping("/api/v1/search/suggestions")
    fun searchSuggestions(
        @RequestParam keyword: String,
    ): ResponseEntity<SearchSuggestionResultDto> =
        ResponseEntity.ok(
            searchService.searchSuggestions(keyword),
        )
}
