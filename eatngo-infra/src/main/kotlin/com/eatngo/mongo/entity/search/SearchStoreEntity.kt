package com.eatngo.mongo.entity.search

import com.eatngo.common.type.Point
import com.eatngo.search.domain.SearchStore
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "SearchStore")
class SearchStoreEntity (
    @Id
    var storeId: Long = 0L,
    var storeName: String = "",
    var storeImage: String = "", // 매장 이미지 S3 URL
    var storeCategory: List<String> = emptyList(),
    var foodCategory: List<String> = emptyList(),
    var roadAddress: String = "",
    @GeoSpatialIndexed
    var location: GeoJsonPoint = GeoJsonPoint(0.0, 0.0),
    var updatedAt: LocalDateTime = LocalDateTime.now(), // 마지막 업데이트 시간
    var createdAt: LocalDateTime = LocalDateTime.now() // 생성 시간
) {
    fun to(): SearchStore {
        return SearchStore(
            storeId = storeId,
            storeName = storeName,
            storeImage = storeImage,
            storeCategory = storeCategory,
            foodCategory = foodCategory,
            roadAddress = roadAddress,
            location = toPoint(location),
            updatedAt = updatedAt,
            createdAt = createdAt
        )
    }

    companion object {
        fun from(searchStore: SearchStore): SearchStoreEntity {
            return SearchStoreEntity(
                storeId = searchStore.storeId,
                storeName = searchStore.storeName,
                storeImage = searchStore.storeImage,
                storeCategory = searchStore.storeCategory,
                foodCategory = searchStore.foodCategory,
                roadAddress = searchStore.roadAddress,
                location = toGeoJsonPoint(
                    searchStore.location.lat,
                    searchStore.location.lng
                ),
                updatedAt = searchStore.updatedAt,
                createdAt = searchStore.createdAt
            )
        }

        fun toGeoJsonPoint(lat: Double, lng: Double): GeoJsonPoint {
            return GeoJsonPoint(lng, lat)
        }

        fun toPoint(geoJsonPoint: GeoJsonPoint): Point {
            return Point(
                lat = geoJsonPoint.coordinates[1],
                lng = geoJsonPoint.coordinates[0]
            )
        }
    }
}