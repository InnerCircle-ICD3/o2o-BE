package com.eatngo.search

import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.type.CoordinateVO
import com.eatngo.search.dto.SearchStoreMapResultDto
import com.eatngo.search.dto.SearchStoreResultDto
import com.eatngo.search.dto.SearchSuggestionResultDto
import com.eatngo.search.dto.StoreFilterDto
import com.eatngo.search.service.SearchService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
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
        summary = "DEPRECATED: 매장 리스트 조회 API -> 삭제 예정!! /api/v1/search/store를 이용해주세요",
        description = "DEPRECATED",
    )
    @GetMapping("/api/v1/store/list")
    fun listStore(
        @Parameter(description = "중심 좌표의 위도 (latitude)", example = "37.572859")
        @RequestParam latitude: Double,
        @Parameter(description = "중심 좌표의 경도 (latitude)", example = "126.971428")
        @RequestParam longitude: Double,
        @Parameter(description = "(optional) 검색반경(Km)\n디폴트는 2.0", example = "2.0")
        @RequestParam searchDistance: Double?,
        @Parameter(description = "(optional) 필터링할 매장 카테고리\nnull일 경우 모든 카테고리 조회")
        @RequestParam storeCategory: StoreEnum.StoreCategory?,
        @Parameter(description = "(optional) HH:mm 형식의 시간\nnull일 경우 모든 픽업시간 조회")
        @RequestParam time: String?, // TODO: VO로 정의하여 검증 로직 추가
        @Parameter(description = "(optional) 픽업 가능 매장만 조회 여부\nnull, false시 모든 상태 조회", example = "true")
        @RequestParam onlyReservable: Boolean = false,
        // TODO : 우선 BE, FE 모두 page+size로 구현 => 추후 개선
        @Parameter(description = "페이지 번호", example = "0")
        @RequestParam page: Int = 0,
        @Parameter(description = "페이지 사이즈", example = "20")
        @RequestParam size: Int = 20,
    ): ResponseEntity<SearchStoreResultDto> =
        ResponseEntity.ok(
            searchService.searchStore(
                StoreFilterDto.from(
                    latitude = latitude,
                    longitude = longitude,
                    storeCategory = storeCategory?.category,
                    time = time,
                    onlyReservable = onlyReservable,
                ),
                // TODO: 검색반경 프론트와 논의 필요
                searchDistance = searchDistance ?: 2.0, // 디폴트 검색반경 2km
                page = page,
                size = size,
            ),
        )

    @Operation(summary = "가게 검색 API", description = "매장 텍스트 검색 API")
    @GetMapping("/api/v1/search/store")
    fun searchStoreKeyword(
        @Parameter(description = "중심 좌표의 위도 (latitude)", example = "37.572859")
        @RequestParam latitude: Double,
        @Parameter(description = "중심 좌표의 경도 (latitude)", example = "126.971428")
        @RequestParam longitude: Double,
        @Parameter(description = "(optional) 검색반경(Km)\n디폴트는 2.0", example = "2.0")
        @RequestParam searchDistance: Double?,
        @Parameter(description = "검색어", example = "쿠키")
        @RequestParam searchText: String,
        @Parameter(description = "(optional) 필터링할 매장 카테고리\nnull일 경우 모든 카테고리 조회")
        @RequestParam storeCategory: StoreEnum.StoreCategory?,
        @Parameter(description = "(optional) HH:mm 형식의 시간\nnull일 경우 모든 픽업시간 조회")
        @RequestParam time: String?, // TODO: VO로 정의하여 검증 로직 추가
        @Parameter(description = "(optional) 픽업 가능 매장만 조회 여부\nnull, false시 모든 상태 조회", example = "true")
        @RequestParam onlyReservable: Boolean = false,
        // TODO : 우선 BE, FE 모두 page+size로 구현 => 추후 개선
        @Parameter(description = "페이지 번호", example = "0")
        @RequestParam page: Int = 0,
        @Parameter(description = "페이지 사이즈", example = "20")
        @RequestParam size: Int = 20,
    ): ResponseEntity<SearchStoreResultDto> =
        ResponseEntity.ok(
            searchService.searchStore(
                StoreFilterDto.from(
                    latitude = latitude,
                    longitude = longitude,
                    searchText = searchText,
                    storeCategory = storeCategory?.category,
                    time = time,
                    onlyReservable = onlyReservable,
                ),
                // TODO: 검색반경 프론트와 논의 필요
                searchDistance = searchDistance ?: 2.0, // 디폴트 검색반경 2km
                page = page,
                size = size,
            ),
        )

    @Operation(summary = "지도 검색 API", description = "지도에서 매장 포인트 리턴 API")
    @GetMapping("/api/v1/search/store/map")
    fun searchStoreMap(
        @Parameter(description = "중심 좌표의 위도 (latitude)", example = "37.566500")
        @RequestParam latitude: Double,
        @Parameter(description = "중심 좌표의 경도 (latitude)", example = "126.978011")
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
        @Parameter(description = "검색어", example = "치킨")
        @RequestParam keyword: String,
    ): ResponseEntity<SearchSuggestionResultDto> =
        ResponseEntity.ok(
            searchService.searchSuggestions(keyword),
        )

    /**
     * 지도 검색 리프레시 API
     * 이 API는 지도에서 매장 포인트를 새로고침하여 최신 정보를 제공합니다.
     * @param latitude 중심 좌표의 위도
     * @param longitude 중심 좌표의 경도
     */
    @Operation(summary = "지도 검색 리프레시 API", description = "지도에서 매장 포인트를 새로고침하여 최신 정보를 제공합니다. TODO : 사용 할 지 논의 필요(우선 만들어둠)")
    @GetMapping("/api/v1/search/store/map/refresh")
    fun searchStoreMapRefresh(
        @Parameter(description = "중심 좌표의 위도 (latitude)", example = "37.566500")
        @RequestParam latitude: Double,
        @Parameter(description = "중심 좌표의 경도 (latitude)", example = "126.978011")
        @RequestParam longitude: Double,
    ): ResponseEntity<SearchStoreMapResultDto> =
        ResponseEntity.ok(
            searchService.searchStoreMapRefresh(
                CoordinateVO.from(
                    latitude = latitude,
                    longitude = longitude,
                ),
            ),
        )
}
