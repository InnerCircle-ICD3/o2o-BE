package com.eatngo.store.mock

import com.eatngo.store.dto.*
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.random.Random

object StoreMockData {
    private val random = Random(System.currentTimeMillis())

    fun createStoreDetailResponse(storeId: Long): StoreDetailResponse {
        return StoreDetailResponse(
            id = storeId,
            name = "맛있는 가게 ${storeId}",
            mainImageUrl = "https://example.com/store${storeId}.jpg",
            contact = "02-${random.nextInt(1000, 9999)}-${random.nextInt(1000, 9999)}",
            description = "맛있는 음식을 제공하는 가게입니다.",
            businessNumber = "${random.nextInt(100, 999)}-${random.nextInt(10, 99)}-${random.nextInt(10000, 99999)}",
            businessHours = listOf(
                BusinessHourResponse("MONDAY", "09:00", "18:00"),
                BusinessHourResponse("TUESDAY", "09:00", "18:00"),
                BusinessHourResponse("WEDNESDAY", "09:00", "18:00"),
                BusinessHourResponse("THURSDAY", "09:00", "18:00"),
                BusinessHourResponse("FRIDAY", "09:00", "18:00")
            ),
            latitude = 37.5665 + random.nextDouble(-0.1, 0.1),
            longitude = 126.9780 + random.nextDouble(-0.1, 0.1),
            pickupStartTime = LocalTime.of(12, 0),
            pickupEndTime = LocalTime.of(13, 0),
            pickupDay = "MONDAY",
            status = "OPEN",
            ratingAverage = random.nextDouble(3.0, 5.0),
            ratingCount = random.nextInt(10, 100),
            foodCategory = listOf("한식", "분식", "카페"),
            storeCategory = listOf("픽업", "할인")
        )
    }

    fun createStoreCUDResponse(storeId: Long): StoreCUDResponse {
        return StoreCUDResponse(
            storeId = storeId,
            actionTime = LocalDateTime.now()
        )
    }

    fun createSubscriptionResponseForStoreOwner(storeId: Long): SubscriptionResponseForStoreOwner {
        return SubscriptionResponseForStoreOwner(
            id = random.nextLong(1, 1000),
            userId = random.nextLong(1, 1000),
            storeId = storeId,
            subscribed = random.nextBoolean(),
            actionTime = LocalDateTime.now(),
            userName = "사용자${random.nextInt(1, 1000)}"
        )
    }

    fun createStoreCreateRequest(): StoreCreateRequest {
        return StoreCreateRequest(
            name = "새로운 가게",
            businessNumber = "${random.nextInt(100, 999)}-${random.nextInt(10, 99)}-${random.nextInt(10000, 99999)}",
            roadFullAddress = "서울시 강남구 역삼동 123-45",
            roadZoneNo = "12345",
            roadBuildingName = "테스트빌딩",
            legalFullAddress = "서울시 강남구 역삼동 123-45",
            legalMainAddressNo = "123",
            legalSubAddressNo = "45",
            adminFullAddress = "서울시 강남구 역삼동",
            latitude = 37.5665,
            longitude = 126.9780,
            pickupStartTime = LocalTime.of(12, 0),
            pickupEndTime = LocalTime.of(13, 0),
            pickupDay = "MONDAY",
            businessHours = listOf(
                BusinessHourDto(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(18, 0)),
                BusinessHourDto(DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(18, 0)),
                BusinessHourDto(DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(18, 0)),
                BusinessHourDto(DayOfWeek.THURSDAY, LocalTime.of(9, 0), LocalTime.of(18, 0)),
                BusinessHourDto(DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(18, 0))
            ),
            contact = "02-${random.nextInt(1000, 9999)}-${random.nextInt(1000, 9999)}",
            description = "맛있는 음식을 제공하는 가게입니다.",
            mainImageUrl = "https://example.com/store.jpg",
            storeCategory = listOf("픽업", "할인"),
            foodCategory = listOf("한식", "분식", "카페")
        )
    }

    fun createStoreUpdateRequest(): StoreUpdateRequest {
        return StoreUpdateRequest(
            name = "수정된 가게",
            businessNumber = "${random.nextInt(100, 999)}-${random.nextInt(10, 99)}-${random.nextInt(10000, 99999)}",
            roadFullAddress = "서울시 서초구 서초동 456-78",
            roadZoneNo = "67890",
            roadBuildingName = "수정된빌딩",
            legalFullAddress = "서울시 서초구 서초동 456-78",
            legalMainAddressNo = "456",
            legalSubAddressNo = "78",
            adminFullAddress = "서울시 서초구 서초동",
            latitude = 37.4837,
            longitude = 127.0324,
            pickupStartTime = LocalTime.of(13, 0),
            pickupEndTime = LocalTime.of(14, 0),
            pickupDay = "TUESDAY",
            businessHours = listOf(
                BusinessHourDto(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(19, 0)),
                BusinessHourDto(DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(19, 0)),
                BusinessHourDto(DayOfWeek.WEDNESDAY, LocalTime.of(10, 0), LocalTime.of(19, 0)),
                BusinessHourDto(DayOfWeek.THURSDAY, LocalTime.of(10, 0), LocalTime.of(19, 0)),
                BusinessHourDto(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(19, 0))
            ),
            contact = "02-${random.nextInt(1000, 9999)}-${random.nextInt(1000, 9999)}",
            description = "수정된 설명입니다.",
            mainImageUrl = "https://example.com/store_updated.jpg",
            storeCategory = listOf("픽업", "할인", "신규"),
            foodCategory = listOf("중식", "일식", "양식")
        )
    }
}