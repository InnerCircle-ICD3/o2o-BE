package com.eatngo.common.exception

import com.eatngo.common.error.BusinessErrorCode
import com.eatngo.common.type.CoordinateVO
import com.eatngo.search.dto.SearchFilter
import org.slf4j.event.Level

/**
 * 검색 관련 예외
 */
open class SearchException(
    override val errorCode: BusinessErrorCode,
    override val message: String = errorCode.message,
    override val data: Map<String, Any>? = null,
    override val logLevel: Level = Level.WARN,
) : BusinessException(errorCode, message, data, logLevel) {

    class SearchInvalidCoordinate(
        latitude: Double,
        longitude: Double,
    ) : SearchException(
        errorCode = BusinessErrorCode.SEARCH_INVALID_COORDINATE,
        message = "${BusinessErrorCode.SEARCH_INVALID_COORDINATE.message} (latitude: $latitude, longitude: $longitude)",
        data = mapOf("latitude" to latitude, "longitude" to longitude),
    )

    class SearchInvalidFilter :
        SearchException(
            errorCode = BusinessErrorCode.SEARCH_INVALID_FILTER,
            message = "${BusinessErrorCode.SEARCH_INVALID_FILTER.message} ",
        )

    class SearchStoreListFailed(
        coordinate: CoordinateVO,
        searchFilter: SearchFilter,
    ) : SearchException(
        errorCode = BusinessErrorCode.SEARCH_STORE_LIST_FAILED,
        message = "${BusinessErrorCode.SEARCH_STORE_LIST_FAILED.message} (coordinate: $coordinate, searchFilter: $searchFilter)",
        data = mapOf("coordinate" to coordinate, "searchFilter" to searchFilter),
    )

    class SearchStoreSearchFailed(
        coordinate: CoordinateVO,
        searchText: String,
    ) : SearchException(
        errorCode = BusinessErrorCode.SEARCH_STORE_SEARCH_FAILED,
        message = "${BusinessErrorCode.SEARCH_STORE_SEARCH_FAILED.message} (coordinate: $coordinate, searchText: $searchText)",
        data = mapOf("coordinate" to coordinate, "searchText" to searchText),
    )

    class SearchStoreMapFailed(
        coordinate: CoordinateVO,
    ) : SearchException(
        errorCode = BusinessErrorCode.SEARCH_STORE_MAP_FAILED,
        message = "${BusinessErrorCode.SEARCH_STORE_MAP_FAILED.message} (coordinate: $coordinate)",
        data = mapOf("coordinate" to coordinate),
    )

    class SearchStoreMapCacheFailed(
        key: String,
    ) : SearchException(
        errorCode = BusinessErrorCode.SEARCH_STORE_MAP_CACHE_FAILED,
        message = "${BusinessErrorCode.SEARCH_STORE_MAP_CACHE_FAILED.message} (key: $key)",
        data = mapOf("key" to key),
    )

    class SearchSuggestionFailed(
        keyword: String,
    ) : SearchException(
        errorCode = BusinessErrorCode.SEARCH_SUGGESTION_FAILED,
        message = "${BusinessErrorCode.SEARCH_SUGGESTION_FAILED.message} (keyword: $keyword)",
        data = mapOf("keyword" to keyword),
    )

    class SearchCategoryNotFound(
        category: String,
    ) : SearchException(
        errorCode = BusinessErrorCode.SEARCH_CATEGORY_NOT_FOUND,
        message = "${BusinessErrorCode.SEARCH_CATEGORY_NOT_FOUND.message} (category: $category)",
        data = mapOf("category" to category),
    )
}
