package com.eatngo.search

import com.eatngo.common.type.Point
import com.eatngo.search.dto.SearchFilter
import com.eatngo.search.dto.SearchStoreMapResultDto
import com.eatngo.search.dto.SearchStoreQueryDto
import com.eatngo.search.dto.SearchStoreResultDto
import com.eatngo.search.dto.StoreStatus
import com.eatngo.search.service.SearchService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.ZonedDateTime

@Tag(name = "Search", description = "검색 API")
@RestController
class SearchController (
    private val searchService: SearchService
) {

    // TODO : 공통 Response로 묶기
    @Operation(summary = "가게 검색 API", description = "매장 리스트 리턴 및 검색 API")
    @GetMapping("/api/v1/search/store")
    fun searchStore(@RequestParam lat: Double,
                    @RequestParam lng: Double,
                    @RequestParam searchText: String?,
                    @RequestParam category: String?,
                    @RequestParam time: ZonedDateTime?,
                    @RequestParam status: StoreStatus= StoreStatus.ALL,
                    @RequestParam page: Int = 0,
                    @RequestParam size: Int = 20,
    ): ResponseEntity<SearchStoreResultDto> {
        val searchResult = searchService.searchStore(
            SearchStoreQueryDto(
                viewPoint = Point(
                    lat = lat,
                    lng = lng
                ),
                filter = SearchFilter(
                    category = category,
                    time = time,
                    searchText = searchText,
                    status = status.statusCode
                )
            ),
            page = page,
            size = size
        )
        return ResponseEntity.ok(
            searchResult
        )
    }

    @Operation(summary = "지도 검색 API", description = "지도에서 매장 포인트 리턴 API")
    @GetMapping("/api/v1/search/store/map")
    fun searchStoreMap(@RequestParam lat: Double, @RequestParam lng: Double): ResponseEntity<SearchStoreMapResultDto> {
        return ResponseEntity.ok(
            searchService.searchStoreMap(
                SearchStoreQueryDto(
                    viewPoint = Point(
                        lat = lat,
                        lng = lng
                    ),
                )
            )
        )
    }

    /**
     * 검색어 자동완성 API
     * @param keyword 검색어
     * @return 검색어 자동완성 리스트
     */
    @Operation(summary = "검색어 자동완성 API", description = "검색어 자동완성 API ex: '치킨' -> '치킨, 치킨너겟'")
    @GetMapping("/api/v1/search/suggestions")
    fun searchSuggestions(@RequestParam keyword: String): ResponseEntity<List<String>> {
        return ResponseEntity.ok(
            searchService.searchSuggestions(keyword)
        )
    }
}