package com.eatngo.common.exception

import com.eatngo.common.error.BusinessErrorCode
import com.eatngo.search.dto.SearchStoreQueryDto

/**
 * 검색 관련 예외
 */
open class SearchException(
    val errorCode: BusinessErrorCode,
    override val message: String = errorCode.message,
    val data: Map<String, Any>? = null,
) : RuntimeException(message) {
    class SearchInvalidCoordinate(
        latitude: Double,
        longitude: Double,
    ) : SearchException(
            BusinessErrorCode.SEARCH_INVALID_COORDINATE,
            "${BusinessErrorCode.SEARCH_INVALID_COORDINATE.message} (latitude: $latitude, longitude: $longitude)",
            mapOf("latitude" to latitude, "longitude" to longitude),
        )

    class SearchInvalidFilter :
        SearchException(
            BusinessErrorCode.SEARCH_INVALID_FILTER,
            "${BusinessErrorCode.SEARCH_INVALID_FILTER.message} ",
        )

    class SearchStoreListFailed(
        searchQuery: SearchStoreQueryDto,
    ) : SearchException(
            BusinessErrorCode.SEARCH_STORE_LIST_FAILED,
            "${BusinessErrorCode.SEARCH_STORE_LIST_FAILED.message} (searchQuery: $searchQuery)",
            mapOf("searchQuery" to searchQuery),
        )

    class SearchStoreMapFailed(
        searchQuery: SearchStoreQueryDto,
    ) : SearchException(
            BusinessErrorCode.SEARCH_STORE_MAP_FAILED,
            "${BusinessErrorCode.SEARCH_STORE_MAP_FAILED.message} (searchQuery: $searchQuery)",
            mapOf("searchQuery" to searchQuery),
        )

    class SearchStoreMapCacheFailed(
        key: String,
    ) : SearchException(
            BusinessErrorCode.SEARCH_STORE_MAP_CACHE_FAILED,
            "${BusinessErrorCode.SEARCH_STORE_MAP_CACHE_FAILED.message} (key: $key)",
            mapOf("key" to key),
        )

    class SearchSuggestionFailed(
        keyword: String,
    ) : SearchException(
            BusinessErrorCode.SEARCH_SUGGESTION_FAILED,
            "${BusinessErrorCode.SEARCH_SUGGESTION_FAILED.message} (keyword: $keyword)",
            mapOf("keyword" to keyword),
        )
}
