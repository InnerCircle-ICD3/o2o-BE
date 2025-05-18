//package com.eatngo.store.mock
//
//import com.eatngo.store.constant.StoreEnum
//import com.eatngo.store.dto.*
//import java.time.LocalTime
//import java.time.ZonedDateTime
//import java.util.*
//import kotlin.random.Random
//
///**
// * Mock 데이터 생성 클래스
// */
//object StoreMockData {
//
//    private val storeNames = listOf(
//        "맛있는 베이커리", "신선한 마트", "쿠키 프레시", "사랑의 빵집", "우리 동네 베이커리",
//        "황금 빵집", "천국의 맛", "클래식 베이커리", "프라임 마트", "아침이슬 빵집"
//    )
//
//    private val descriptions = listOf(
//        "신선한 재료로 만든 빵과 디저트를 판매합니다.",
//        "매일 아침 구워내는 신선한 빵이 대표 메뉴입니다.",
//        "전통적인 방식으로 제빵하는 품격있는 빵집입니다.",
//        "다양한 종류의 빵과 케이크를 만나보세요.",
//        "지역 주민들이 사랑하는 향수를 불러일으키는 빵집입니다."
//    )
//
//    private val categories = listOf(
//        "베이커리", "케이크", "디저트", "커피", "샌드위치",
//        "빵", "마트", "편의점", "카페", "분식"
//    )
//
//    private val addresses = listOf(
//        "서울시 강남구 역삼동 123-45",
//        "서울시 서초구 서초동 456-78",
//        "서울시 종로구 종로1가 89-12",
//        "서울시 마포구 합정동 345-67",
//        "서울시 강서구 화곡동 890-12"
//    )
//
//    /**
//     * 랜덤 Store 응답 객체 생성
//     */
//    fun createRandomStore(storeId: Long = Random.nextLong(1, 100)): StoreDto {
//        val name = storeNames.random()
//        val description = descriptions.random()
//        val category = categories.shuffled().take(Random.nextInt(1, 3))
//
//        return StoreDto(
//            storeId = storeId,
//            name = name,
//            description = description,
//            mainImageUrl = "https://example.com/images/store$storeId.jpg",
//            businessNumber = "123-45-${Random.nextInt(10000, 99999)}",
//            contact = "02-${Random.nextInt(1000, 9999)}-${Random.nextInt(1000, 9999)}",
//            roadAddress = RoadAddressDto(
//                fullAddress = "도로명 " + addresses.random(),
//                zoneNo = "${Random.nextInt(10000, 99999)}",
//                buildingName = "건물명 ${Random.nextInt(1, 100)}"
//            ),
//            legalAddress = LegalAddressDto(
//                fullAddress = addresses.random(),
//                mainAddressNo = "${Random.nextInt(1, 100)}",
//                subAddressNo = "${Random.nextInt(1, 100)}"
//            ),
//            adminAddress = AdminAddressDto(
//                fullAddress = "행정동 테스트"
//            ),
//            location = LocationDto(
//                lat = 37.4 + Random.nextDouble(0.0, 0.3),
//                lng = 127.0 + Random.nextDouble(0.0, 0.3)
//            ),
//            status = StoreEnum.StoreStatus.values().random(),
//            categories = category,
//            businessHours = listOf(
//                BusinessHourDto(
//                    dayOfWeek = "MONDAY",
//                    openTime = "09:00",
//                    closeTime = "18:00"
//                ),
//                BusinessHourDto(
//                    dayOfWeek = "TUESDAY",
//                    openTime = "09:00",
//                    closeTime = "18:00"
//                )
//            ),
//            pickupStartTime = LocalTime.of(9, 0).toString(),
//            pickupEndTime = LocalTime.of(18, 0).toString(),
//            pickupAvailableForTomorrow = Random.nextBoolean(),
//            isAvailableForPickup = Random.nextBoolean()
//        )
//    }
//
//    /**
//     * 랜덤 Store 목록 생성
//     */
//    fun createRandomStores(count: Int = 10): List<StoreResponse> {
//        return (1..count).map {
//            val store = createRandomStore(it.toLong())
//            StoreResponse(
//                storeId = store.storeId,
//                name = store.name,
//                mainImageUrl = store.mainImageUrl,
//                status = store.status,
//                isAvailableForPickup = store.isAvailableForPickup,
//                pickupAvailableForTomorrow = store.pickupAvailableForTomorrow,
//                distance = Random.nextDouble(0.1, 5.0)
//            )
//        }
//    }
//
//    /**
//     * 랜덤 구독 정보 생성
//     */
//    fun createRandomSubscription(id: String = UUID.randomUUID().toString(), storeId: Long = Random.nextLong(1, 100)): StoreSubscriptionDto {
//        val storeSummary = createRandomStores(5).random()
//
//        return StoreSubscriptionDto(
//            id = id,
//            userId = UUID.randomUUID().toString(),
//            storeId = storeId,
//            createdAt = ZonedDateTime.now().minusDays(Random.nextLong(1, 30)),
//            updatedAt = ZonedDateTime.now(),
//            deletedAt = null,
//            subscribed = true,
//            store = storeSummary
//        )
//    }
//
//    /**
//     * 랜덤 구독 목록 생성
//     */
//    fun createRandomSubscriptions(count: Int = 5): List<StoreSubscriptionSummary> {
//        return (1..count).map {
//            val storeId = Random.nextLong(1, 100)
//            val store = createRandomStore(storeId)
//
//            StoreSubscriptionSummary(
//                id = UUID.randomUUID().toString(),
//                storeId = storeId,
//                userId = UUID.randomUUID().toString(),
//                createdAt = ZonedDateTime.now().minusDays(Random.nextLong(1, 30)),
//                store = StoreResponse(
//                    storeId = store.storeId,
//                    name = store.name,
//                    mainImageUrl = store.mainImageUrl,
//                    status = store.status,
//                    isAvailableForPickup = store.isAvailableForPickup,
//                    pickupAvailableForTomorrow = store.pickupAvailableForTomorrow,
//                    distance = Random.nextDouble(0.1, 5.0)
//                )
//            )
//        }
//    }
//
//    /**
//     * 구독한 매장 목록 생성
//     */
//    fun createSubscribedStores(userId: Long = Random.nextLong(1, 100)): List<StoreResponse> {
//        return createRandomStores(Random.nextInt(3, 8))
//    }
//}