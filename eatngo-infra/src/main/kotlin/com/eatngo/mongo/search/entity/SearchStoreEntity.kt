package com.eatngo.mongo.search.entity

import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.exception.search.SearchException
import com.eatngo.extension.orThrow
import com.eatngo.search.domain.Coordinate
import com.eatngo.search.domain.SearchStore
import com.eatngo.search.domain.SearchStoreStatus
import com.eatngo.search.domain.TimeRange
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.DayOfWeek
import java.time.LocalDateTime

@Document(collection = "SearchStore")
class SearchStoreEntity(
    @Id
    var storeId: Long,
    var storeName: String,
    var storeImage: String, // 매장 이미지 S3 URL
    var storeCategory: List<String>,
    var foodCategory: List<String>, // 대표 판매 음식 종류
    var roadNameAddress: String? = null,
    @GeoSpatialIndexed
    var coordinate: GeoJsonPoint,
    var status: Int, // 매장 오픈 여부
    @Field("businessHours")
    var businessHours: Map<DayOfWeek, TimeRange>,
    var updatedAt: LocalDateTime = LocalDateTime.now(), // 마지막 업데이트 시간
    var createdAt: LocalDateTime = LocalDateTime.now(), // 생성 시간
) {
    fun to(): SearchStore =
        SearchStore(
            storeId = storeId,
            storeName = storeName,
            storeImage = storeImage,
            storeCategory =
            storeCategory.map {
                StoreEnum.StoreCategory.valueOf(it).orThrow {
                    SearchException.SearchCategoryNotFound(it)
                }
            },
            foodCategory = foodCategory,
            roadNameAddress = roadNameAddress,
            coordinate =
            Coordinate.Companion.from(
                latitude = coordinate.coordinates[1],
                longitude = coordinate.coordinates[0],
            ),
            status = SearchStoreStatus.Companion.from(status),
            businessHours = businessHours,
            updatedAt = updatedAt,
            createdAt = createdAt,
        )

    companion object {
        fun toGeoJsonPoint(
            latitude: Double,
            longitude: Double,
        ): GeoJsonPoint = GeoJsonPoint(longitude, latitude)

        fun from(searchStore: SearchStore): SearchStoreEntity =
            SearchStoreEntity(
                storeId = searchStore.storeId,
                storeName = searchStore.storeName,
                storeImage = searchStore.storeImage,
                storeCategory = searchStore.storeCategory.map { it.name },
                foodCategory = searchStore.foodCategory,
                roadNameAddress = searchStore.roadNameAddress,
                coordinate =
                toGeoJsonPoint(
                    latitude = searchStore.coordinate.latitude,
                    longitude = searchStore.coordinate.longitude,
                ),
                status = searchStore.status.code,
                businessHours = searchStore.businessHours,
            )
    }
}
