package com.eatngo.store.dto

import java.time.LocalTime

val mockStoreDetailResponses = mapOf(
    1L to StoreDetailResponse(
        id = 1L,
        name = "딥블루",
        mainImageUrl = "https://eatngo-app.s3.ap-northeast-2.amazonaws.com/store/otter.png",
        contact = "051-222-3333",
        description = "바다가 보이는 감성 카페입니다.",
        businessNumber = "9876512210",
        businessHours = listOf(
            BusinessHourDto(
                dayOfWeek = java.time.DayOfWeek.SUNDAY,
                openTime = LocalTime.of(8, 30),
                closeTime = LocalTime.of(22, 0)
            ),
            BusinessHourDto(
                dayOfWeek = java.time.DayOfWeek.SATURDAY,
                openTime = LocalTime.of(10, 0),
                closeTime = LocalTime.of(23, 0)
            )
        ),
        address = AddressDto(
            roadNameAddress = "북태평양 해저기지",
            lotNumberAddress = "북태평양 해저기지",
            buildingName = null,
            zipCode = "48058",
            region1DepthName = "북태평양",
            region2DepthName = "해저기지",
            region3DepthName = "",
            coordinate = CoordinateDto(
                latitude = 35.170908,
                longitude = 129.130356
            )
        ),
        pickupDay = "TOMORROW",
        todayPickupStartTime = LocalTime.of(8, 30),
        todayPickupEndTime = LocalTime.of(22, 0),
        status = "OPEN",
        ratingAverage = 4.7,
        ratingCount = 102,
        foodCategory = listOf("커피", "디저트"),
        storeCategory = listOf("BREAD")
    ),
    2L to StoreDetailResponse(
        id = 2L,
        name = "그릴하우스",
        mainImageUrl = "https://eatngo-app.s3.ap-northeast-2.amazonaws.com/store/pu.png",
        contact = "02-555-6666",
        description = "정통 스테이크와 그릴 요리를 즐길 수 있는 레스토랑입니다.",
        businessNumber = "1234567890",
        businessHours = listOf(
            BusinessHourDto(
                dayOfWeek = java.time.DayOfWeek.MONDAY,
                openTime = LocalTime.of(11, 0),
                closeTime = LocalTime.of(22, 0)
            ),
            BusinessHourDto(
                dayOfWeek = java.time.DayOfWeek.FRIDAY,
                openTime = LocalTime.of(11, 0),
                closeTime = LocalTime.of(23, 0)
            )
        ),
        address = AddressDto(
            roadNameAddress = "서울특별시 강동구 천호대로 123",
            lotNumberAddress = "강동구 천호동 456-78",
            buildingName = "그릴타워",
            zipCode = "05234",
            region1DepthName = "서울특별시",
            region2DepthName = "강동구",
            region3DepthName = "천호동",
            coordinate = CoordinateDto(
                latitude = 37.538377,
                longitude = 127.123456
            )
        ),
        pickupDay = "TODAY",
        todayPickupStartTime = LocalTime.of(11, 0),
        todayPickupEndTime = LocalTime.of(22, 0),
        status = "OPEN",
        ratingAverage = 4.3,
        ratingCount = 88,
        foodCategory = listOf("스테이크", "파스타", "와인"),
        storeCategory = listOf("RESTAURANT")
    ),
    3L to StoreDetailResponse(
        id = 3L,
        name = "그린샐러드",
        mainImageUrl = "https://eatngo-app.s3.ap-northeast-2.amazonaws.com/store/pu.png",
        contact = "051-777-8888",
        description = "신선한 채소와 건강한 샐러드를 제공하는 샐러드 전문점입니다.",
        businessNumber = "1122334455",
        businessHours = listOf(
            BusinessHourDto(
                dayOfWeek = java.time.DayOfWeek.TUESDAY,
                openTime = LocalTime.of(10, 0),
                closeTime = LocalTime.of(20, 0)
            ),
            BusinessHourDto(
                dayOfWeek = java.time.DayOfWeek.THURSDAY,
                openTime = LocalTime.of(10, 0),
                closeTime = LocalTime.of(20, 0)
            )
        ),
        address = AddressDto(
            roadNameAddress = "부산광역시 해운대구 센텀서로 30",
            lotNumberAddress = "해운대구 우동 1234-5",
            buildingName = "센텀그린",
            zipCode = "48059",
            region1DepthName = "부산광역시",
            region2DepthName = "해운대구",
            region3DepthName = "우동",
            coordinate = CoordinateDto(
                latitude = 35.170001,
                longitude = 129.130001
            )
        ),
        pickupDay = "TOMORROW",
        todayPickupStartTime = LocalTime.of(10, 0),
        todayPickupEndTime = LocalTime.of(20, 0),
        status = "CLOSED",
        ratingAverage = 4.9,
        ratingCount = 56,
        foodCategory = listOf("샐러드", "주스", "샌드위치"),
        storeCategory = listOf("SALAD")
    )
)
