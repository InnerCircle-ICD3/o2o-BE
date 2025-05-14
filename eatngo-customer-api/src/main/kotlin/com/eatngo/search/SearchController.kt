package com.eatngo.search

import com.eatngo.search.dto.SearchStoreDto
import com.eatngo.search.dto.SearchStoreMapRequestDto
import com.eatngo.search.dto.SearchStoreMapResultDto
import com.eatngo.search.dto.SearchStoreRequestDto
import com.eatngo.search.dto.SearchStoreResultDto
import com.eatngo.search.service.SearchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchController (
    private val searchService: SearchService
) {
    @GetMapping("/search/store")
    fun searchStore(@RequestParam searchDto: SearchStoreRequestDto): SearchStoreResultDto {
        // Simulate a search operation
        return searchService.searchStore(
            SearchStoreDto(
                viewPoint = searchDto.viewPoint,
                filter = searchDto.filter
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