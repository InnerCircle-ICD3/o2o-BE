package com.eatngo.store.mock

import com.eatngo.store.dto.*
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

    fun createStoreSubscriptionResponse(storeId: Long): StoreSubscriptionResponse {
        return StoreSubscriptionResponse(
            id = random.nextLong(1, 1000),
            storeId = storeId,
            storeName = "맛있는 가게 ${storeId}",
            mainImageUrl = "https://example.com/store${storeId}.jpg",
            status = "OPEN",
            discountRate = random.nextDouble(0.1, 0.3),
            originalPrice = random.nextInt(10000, 20000),
            discountedPrice = random.nextInt(7000, 15000),
            subscribedAt = LocalDateTime.now().minusDays(random.nextLong(1, 30))
        )
    }

    fun createSubscriptionToggleResponse(storeId: Long): SubscriptionToggleResponse {
        return SubscriptionToggleResponse(
            id = random.nextLong(1, 1000),
            userId = random.nextLong(1, 1000),
            storeId = storeId,
            subscribed = random.nextBoolean(),
            actionTime = LocalDateTime.now()
        )
    }

    fun createStoreResponse(storeId: Long): StoreResponse {
        return StoreResponse(
            id = storeId,
            name = "맛있는 가게 ${storeId}",
            mainImageUrl = "https://example.com/store${storeId}.jpg",
            status = "OPEN",
            pickupStartTime = LocalTime.of(12, 0),
            pickupEndTime = LocalTime.of(13, 0),
            pickupDay = "MONDAY",
            distance = random.nextDouble(0.1, 5.0),
            ratingAverage = random.nextDouble(3.0, 5.0),
            ratingCount = random.nextInt(10, 100),
            foodCategory = listOf("한식", "분식", "카페")
        )
    }
}