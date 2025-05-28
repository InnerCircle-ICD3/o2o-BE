package com.eatngo.mongo.entity.search

import com.eatngo.common.type.Coordinate
import com.eatngo.search.constant.StoreEnum
import com.eatngo.search.domain.SearchStore
import com.eatngo.search.dto.BusinessHoursDto
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Document(collection = "SearchStore")
class SearchStoreEntity(
    @Id
    var storeId: Long = 0L,
    var storeName: String = "",
    var storeImage: String = "", // 매장 이미지 S3 URL
    var category: List<StoreEnum.StoreCategory> = emptyList(),
    var open: Boolean = true, // 매장 오픈 여부
    var openTime: LocalDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0)), // 매장 오픈 시간
    var closeTime: LocalDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(22, 0)), // 매장 마감 시간
    var roadAddress: String = "",
    @GeoSpatialIndexed
    var location: GeoJsonPoint = GeoJsonPoint(0.0, 0.0),
    var updatedAt: LocalDateTime = LocalDateTime.now(), // 마지막 업데이트 시간
    var createdAt: LocalDateTime = LocalDateTime.now(), // 생성 시간
) {
    fun to(): SearchStore =
        SearchStore(
            storeId = storeId,
            storeName = storeName,
            storeImage = storeImage,
            category = category,
            open = open,
            businessHours =
                BusinessHoursDto(
                    openTime = openTime,
                    closeTime = closeTime,
                ),
            roadAddress = roadAddress,
            location = toPoint(location),
            updatedAt = updatedAt,
            createdAt = createdAt,
        )

    companion object {
        fun from(searchStore: SearchStore): SearchStoreEntity =
            SearchStoreEntity(
                storeId = searchStore.storeId,
                storeName = searchStore.storeName,
                storeImage = searchStore.storeImage,
                category = searchStore.category,
                roadAddress = searchStore.roadAddress,
                open = searchStore.open,
                openTime = searchStore.businessHours.openTime,
                closeTime = searchStore.businessHours.closeTime,
                location =
                    toGeoJsonPoint(
                        searchStore.location.latitude,
                        searchStore.location.longitude,
                    ),
                updatedAt = searchStore.updatedAt,
                createdAt = searchStore.createdAt,
            )

        fun toGeoJsonPoint(
            latitude: Double,
            longitude: Double,
        ): GeoJsonPoint = GeoJsonPoint(longitude, latitude)

        fun toPoint(geoJsonPoint: GeoJsonPoint): Coordinate =
            Coordinate(
                latitude = geoJsonPoint.coordinates[1],
                longitude = geoJsonPoint.coordinates[0],
            )
    }
}
