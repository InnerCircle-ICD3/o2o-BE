package com.eatngo.search

import com.eatngo.search.dto.SearchStoreDto
import com.eatngo.search.dto.SearchStoreMapRequestDto
import com.eatngo.search.dto.SearchStoreMapResultDto
import com.eatngo.search.dto.SearchStoreRequestDto
import com.eatngo.search.dto.SearchStoreResponseDto
import com.eatngo.search.service.SearchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * SearchController.kt
 * @author : 이다영
 * @date : 2025-05-14
 * @description : 검색 관련 컨트롤러
 */
@RestController
class SearchController (
    private val searchService: SearchService
) {
    /**
     * 리스트 검색 API
     */
    @GetMapping("/search/store")
    fun searchStore(@RequestParam searchDto: SearchStoreRequestDto): SearchStoreResponseDto {
        return SearchStoreResponseDto(
            success = true,
            data = searchService.searchStore(
                SearchStoreDto(
                    viewPoint = searchDto.viewPoint,
                    filter = searchDto.filter
                )
            )
        )
    }

    @GetMapping("/search/store/map")
    fun searchStoreMap(@RequestParam searchDto: SearchStoreMapRequestDto): SearchStoreMapResultDto {
        // Simulate a search operation
        return searchService.searchStoreMap(
            SearchStoreDto(
                viewPoint = searchDto.viewPoint
            )
        )
    }
}