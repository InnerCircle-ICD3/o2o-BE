package com.eatngo.search

import com.eatngo.search.dto.Point
import com.eatngo.search.dto.SearchFilter
import com.eatngo.search.dto.SearchStoreDto
import com.eatngo.search.dto.SearchStoreMapRequestDto
import com.eatngo.search.dto.SearchStoreMapResponseDto
import com.eatngo.search.dto.SearchStoreRequestDto
import com.eatngo.search.dto.SearchStoreResponseDto
import com.eatngo.search.service.SearchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
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
    fun searchStore(@ModelAttribute searchDto: SearchStoreRequestDto): SearchStoreResponseDto {
        return SearchStoreResponseDto(
            success = true,
            data = searchService.searchStore(
                SearchStoreDto(
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

    /**
     * 지도 검색 API
     */
    @GetMapping("/search/store/map")
    fun searchStoreMap(@RequestParam searchDto: SearchStoreMapRequestDto): SearchStoreMapResponseDto {
        return SearchStoreMapResponseDto(
            success = true,
            data = searchService.searchStoreMap(
                SearchStoreDto(
                    viewPoint = searchDto.viewPoint
                )
            )
        )
    }
}