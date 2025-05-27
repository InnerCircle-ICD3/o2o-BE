package com.eatngo.search

import com.eatngo.common.type.Coordinate
import com.eatngo.search.constant.StoreEnum
import com.eatngo.search.dto.*
import com.eatngo.search.service.SearchService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@Tag(name = "Search", description = "검색 API")
@RestController
class SearchController(
    private val searchService: SearchService,
) {
    // TODO : 공통 Response로 묶기
    @Operation(summary = "가게 검색 API", description = "매장 리스트 리턴 및 검색 API")
    @GetMapping("/api/v1/search/store")
    fun searchStore(
        @RequestParam lat: Double,
        @RequestParam lng: Double,
        @RequestParam searchText: String?,
        @RequestParam category: String?,
        @RequestParam time: LocalDateTime?,
        @RequestParam status: StoreStatus = StoreStatus.ALL,
        // TODO : 우선 BE, FE 모두 page+size로 구현 => 추후 개선
        @RequestParam page: Int = 0,
        @RequestParam size: Int = 20,
    ): ResponseEntity<SearchStoreResultDto> {
        val searchResult =
            searchService.searchStore(
                SearchStoreQueryDto(
                    viewCoordinate =
                        Coordinate(
                            lat = lat,
                            lng = lng,
                        ),
                    filter =
                        SearchFilter(
                            category = StoreEnum.StoreCategory.fromString(category),
                            time = time,
                            searchText = searchText,
                            status = status.statusCode,
                        ),
                ),
                page = page,
                size = size,
            )
        return ResponseEntity.ok(
            searchResult,
        )
    }

    @Operation(summary = "지도 검색 API", description = "지도에서 매장 포인트 리턴 API")
    @GetMapping("/api/v1/search/store/map")
    fun searchStoreMap(
        @RequestParam lat: Double,
        @RequestParam lng: Double,
    ): ResponseEntity<SearchStoreMapResultDto> =
        ResponseEntity.ok(
            searchService.searchStoreMap(
                SearchStoreQueryDto(
                    viewCoordinate =
                        Coordinate(
                            lat = lat,
                            lng = lng,
                        ),
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
    ): ResponseEntity<List<String>> =
        ResponseEntity.ok(
            searchService.searchSuggestions(keyword),
        )
}
