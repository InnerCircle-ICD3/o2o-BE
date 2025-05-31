package com.eatngo.common.exception

import com.eatngo.common.error.BusinessErrorCode
import com.eatngo.common.type.CoordinateVO
import com.eatngo.search.dto.SearchFilter

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
        coordinate: CoordinateVO,
        searchFilter: SearchFilter,
    ) : SearchException(
            BusinessErrorCode.SEARCH_STORE_LIST_FAILED,
            "${BusinessErrorCode.SEARCH_STORE_LIST_FAILED.message} (coordinate: $coordinate, searchFilter: $searchFilter)",
            mapOf("coordinate" to coordinate, "searchFilter" to searchFilter),
        )

    class SearchStoreSearchFailed(
        coordinate: CoordinateVO,
        searchText: String,
    ) : SearchException(
            BusinessErrorCode.SEARCH_STORE_SEARCH_FAILED,
            "${BusinessErrorCode.SEARCH_STORE_SEARCH_FAILED.message} (coordinate: $coordinate, searchText: $searchText)",
            mapOf("coordinate" to coordinate, "searchText" to searchText),
        )

    class SearchStoreMapFailed(
        coordinate: CoordinateVO,
    ) : SearchException(
            BusinessErrorCode.SEARCH_STORE_MAP_FAILED,
            "${BusinessErrorCode.SEARCH_STORE_MAP_FAILED.message} (coordinate: $coordinate)",
            mapOf("coordinate" to coordinate),
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

    class SearchCategoryNotFound(
        category: String,
    ) : SearchException(
            BusinessErrorCode.SEARCH_CATEGORY_NOT_FOUND,
            "${BusinessErrorCode.SEARCH_CATEGORY_NOT_FOUND.message} (category: $category)",
            mapOf("category" to category),
        )
}
