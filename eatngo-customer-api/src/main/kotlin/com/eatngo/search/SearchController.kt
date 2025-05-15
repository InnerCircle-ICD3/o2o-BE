package com.eatngo.search

import com.eatngo.common.response.ApiResponse
import com.eatngo.common.type.Point
import com.eatngo.search.dto.SearchStoreMapResultDto
import com.eatngo.search.dto.SearchStoreQueryDto
import com.eatngo.search.dto.SearchStoreRequestDto
import com.eatngo.search.dto.SearchStoreResultDto
import com.eatngo.search.service.SearchService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Search", description = "검색 API")
@RestController
class SearchController (
    private val searchService: SearchService
) {

    // TODO : GET vs POST
    @Operation(summary = "가게 검색 API", description = "매장 리스트 리턴 및 검색 API")
    @GetMapping("/search/store")
    fun searchStore(@ModelAttribute searchDto: SearchStoreRequestDto): ApiResponse<SearchStoreResultDto> {
        return ApiResponse.success(
            searchService.searchStore(
                SearchStoreQueryDto(
                    viewPoint = Point(
                        lat = searchDto.lat,
                        lng = searchDto.lng
                    ),
                    filter = searchDto.filter
                ),
                offset = searchDto.offset
            )
        )
    }

    @Operation(summary = "지도 검색 API", description = "지도에서 매장 포인트 리턴 API")
    @GetMapping("/search/store/map")
    fun searchStoreMap(@RequestParam lat: Double, @RequestParam lng: Double): ApiResponse<SearchStoreMapResultDto> {
        return ApiResponse.success(
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

    @Operation(summary = "검색어 자동완성 API", description = "검색어 자동완성 API ex: '치킨' -> '치킨, 치킨너겟'")
    @GetMapping("/search/suggestions")
    fun searchSuggestions(@RequestParam keyword: String): ApiResponse<List<String>> {
        return ApiResponse.success(
            searchService.searchSuggestions(keyword)
        )
    }
}