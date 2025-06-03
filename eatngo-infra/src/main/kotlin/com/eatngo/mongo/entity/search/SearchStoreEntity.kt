package com.eatngo.mongo.entity.search

import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.exception.SearchException
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
    var roadNameAddress: String,
    @GeoSpatialIndexed
    var coordinate: GeoJsonPoint,
    var status: Int, // 매장 오픈 여부
    @Field("businessHours")
    var businessHours: Map<DayOfWeek, TimeRange>,
    var pickUpDay: String,
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
                Coordinate.from(
                    latitude = coordinate.coordinates[1],
                    longitude = coordinate.coordinates[0],
                ),
            status = SearchStoreStatus.from(status),
            businessHours = businessHours,
            pickUpDay = StoreEnum.PickupDay.valueOf(pickUpDay),
            pickupHour =
                TimeRange(
                    "00:00",
                    "23:59",
                ),
            // TODO: 오늘 날짜 BusinessHours에 맞게 변경 필요
            updatedAt = updatedAt,
            createdAt = createdAt,
        )

    companion object {
        fun toGeoJsonPoint(
            latitude: Double,
            longitude: Double,
        ): GeoJsonPoint = GeoJsonPoint(longitude, latitude)
    }
}
