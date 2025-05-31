//package com.eatngo.store.controller
//
//import com.appmattus.kotlinfixture.kotlinFixture
//import com.eatngo.common.response.ApiResponse
//import com.eatngo.common.constant.StoreEnum
//import com.eatngo.store.dto.*
//import com.eatngo.store.service.StoreService
//import io.kotest.core.spec.style.DescribeSpec
//import io.kotest.matchers.shouldBe
//import io.kotest.matchers.types.shouldBeTypeOf
//import io.mockk.coEvery
//import io.mockk.coVerify
//import io.mockk.mockk
//import org.springframework.security.core.userdetails.User
//import org.springframework.security.core.userdetails.UserDetails
//import java.time.DayOfWeek
//
//class StoreControllerTest : DescribeSpec({
//    val storeService = mockk<StoreService>()
//    val storeController = StoreController(storeService)
//    val fixture = kotlinFixture()
//
//    // 테스트 사용자 정보
//    val ownerId = "owner123"
//    val userDetails: UserDetails = User
//        .withUsername(ownerId)
//        .password("password")
//        .authorities(emptyList())
//        .build()
//
//    // 테스트용 데이터 준비
//    val storeId: Long = 1
//    val basicBusinessHours = listOf(
//        BusinessHourDto(
//            dayOfWeek = DayOfWeek.MONDAY.toString(),
//            openTime = "09:00",
//            closeTime = "18:00"
//        )
//    )
//
//    val roadAddress = RoadAddressDto(
//        fullAddress = "서울특별시 강남구 테헤란로 123",
//        zipCode = "12345",
//        buildingName = "테스트빌딩"
//    )
//
//    val legalAddress = LegalAddressDto(
//        fullAddress = "서울특별시 강남구 역삼동 123-45",
//        mainAddressNo = "123",
//        subAddressNo = "45"
//    )
//
//    val adminAddress = AdminAddressDto(
//        fullAddress = "행정동 테스트"
//    )
//
//    val location = LocationDto(
//        lat = 37.123456,
//        lng = 127.123456
//    )
//
//    val summaryResponse = StoreResponse(
//        storeId = storeId,
//        name = "테스트 매장",
//        mainImageUrl = "http://example.com/image.jpg",
//        status = StoreEnum.StoreStatus.OPEN,
//        isAvailableForPickup = true,
//        pickupAvailableForTomorrow = true,
//        distance = 1.5
//    )
//
//    val storeResponse = StoreDto(
//        storeId = storeId,
//        name = "테스트 매장",
//        roadAddress = roadAddress,
//        legalAddress = legalAddress,
//        adminAddress = adminAddress,
//        location = location,
//        businessNumber = "123-45-67890",
//        businessHours = basicBusinessHours,
//        contact = "02-1234-5678",
//        description = "테스트 매장입니다.",
//        pickupStartTime = "10:00",
//        pickupEndTime = "20:00",
//        pickupAvailableForTomorrow = true,
//        mainImageUrl = "http://example.com/image.jpg",
//        status = StoreEnum.StoreStatus.OPEN,
//        isAvailableForPickup = true,
//        categories = listOf("카페", "디저트")
//    )
//
//    val storeDetailResponse = StoreDetailResponse(
//        store = storeResponse,
//        subscribed = false
//    )
//
//    describe("createStore") {
//        val createRequest = StoreCreateDto(
//            name = "새로운 매장",
//            storeOwnerId = "test-owner",
//            roadAddress = roadAddress,
//            legalAddress = legalAddress,
//            location = location,
//            businessNumber = "123-45-67890",
//            businessHours = basicBusinessHours,
//            contact = "02-1234-5678",
//            description = "새로운 테스트 매장입니다.",
//            pickupStartTime = "10:00",
//            pickupEndTime = "20:00",
//            pickupAvailableForTomorrow = true,
//            mainImageUrl = "http://example.com/new-image.jpg",
//            categories = listOf("카페", "베이커리")
//        )
//
//        it("새로운 매장을 등록한다") {
//            // Given
//            coEvery {
//                storeService.createStore(any())
//            } returns storeResponse
//
//            // When
//            val result = storeController.createStore(createRequest)
//
//            // Then
//            result.shouldBeTypeOf<ApiResponse.Success<StoreDto>>()
//            result.data.storeId shouldBe storeId
//
//            coVerify { storeService.createStore(createRequest) }
//        }
//    }
//
//    describe("updateStore") {
//        val updateRequest = StoreUpdateDto(
//            name = "수정된 매장",
//            description = "수정된 매장 설명",
//            pickupStartTime = "09:00",
//            pickupEndTime = "21:00"
//        )
//
//        it("매장 정보를 수정한다") {
//            // Given
//            val updatedResponse = storeResponse.copy(
//                name = "수정된 매장",
//                description = "수정된 매장 설명",
//                pickupStartTime = "09:00",
//                pickupEndTime = "21:00"
//            )
//
//            coEvery { storeService.updateStore(storeId, updateRequest) } returns updatedResponse
//
//            // When
//            val result = storeController.updateStore(storeId, updateRequest)
//
//            // Then
//            result.shouldBeTypeOf<ApiResponse.Success<StoreDto>>()
//            result.data.name shouldBe "수정된 매장"
//            result.data.description shouldBe "수정된 매장 설명"
//
//            coVerify { storeService.updateStore(storeId, updateRequest) }
//        }
//    }
//
//    describe("updateStoreStatus") {
//        val statusUpdateRequest = StatusUpdateRequest(
//            status = StoreEnum.StoreStatus.CLOSED
//        )
//
//        it("매장 상태를 변경한다") {
//            // Given
//            val updatedResponse = storeResponse.copy(
//                status = StoreEnum.StoreStatus.CLOSED
//            )
//
//            coEvery { storeService.updateStoreStatus(storeId, statusUpdateRequest) } returns updatedResponse
//
//            // When
//            val result = storeController.updateStoreStatus(storeId, statusUpdateRequest)
//
//            // Then
//            result.shouldBeTypeOf<ApiResponse.Success<StoreDto>>()
//            result.data.status shouldBe StoreEnum.StoreStatus.CLOSED
//
//            coVerify { storeService.updateStoreStatus(storeId, statusUpdateRequest) }
//        }
//    }
//
//
//    describe("deleteStore") {
//        it("매장을 삭제한다") {
//            // Given
//            coEvery { storeService.deleteStore(storeId) } returns true
//
//            // When
//            val result = storeController.deleteStore(storeId)
//
//            // Then
//            result.shouldBeTypeOf<ApiResponse.Success<Boolean>>()
//            result.data shouldBe true
//
//            coVerify { storeService.deleteStore(storeId) }
//        }
//    }
//
//    describe("getStores") {
//        val searchRequest = CustomerStoreSearchRequest(
//            keyword = "테스트",
//            latitude = 37.123456,
//            longitude = 127.123456,
//            radius = 5.0,
//            category = "카페",
//            onlyOpen = true,
//            availableForPickup = true,
//            availableForTomorrow = false,
//            limit = 10,
//            offset = 0
//        )
//
//        val storeList = listOf(summaryResponse)
//        val listResponse = CustomerStoreListResponse(
//            stores = storeList,
//            totalCount = storeList.size,
//            offset = 0,
//            limit = 10
//        )
//
//        it("주어진 검색 조건으로 상점 목록을 조회한다") {
//            // Given
//            coEvery { storeService.searchStores(searchRequest.toStoreSearchDto()) } returns storeList
//
//            // When
//            val result = storeController.getStores(searchRequest)
//
//            // Then
//            result.shouldBeTypeOf<ApiResponse.Success<CustomerStoreListResponse>>()
//            result.data.stores.size shouldBe 1
//            result.data.stores[0].storeId shouldBe storeId
//            result.data.totalCount shouldBe 1
//            result.data.limit shouldBe 10
//            result.data.offset shouldBe 0
//
//            coVerify { storeService.searchStores(searchRequest.toStoreSearchDto()) }
//        }
//    }
//
//    describe("getStoreDetail") {
//        it("상점 ID로 상점 상세 정보를 조회한다") {
//            // Given
//            coEvery { storeService.getStoreDetail(storeId) } returns storeDetailResponse
//
//            // When
//            val result = storeController.getStoreDetail(storeId)
//
//            // Then
//            result.shouldBeTypeOf<ApiResponse.Success<StoreDetailResponse>>()
//            result.data.store.storeId shouldBe storeId
//            result.data.store.name shouldBe "테스트 매장"
//            result.data.store.categories shouldBe listOf("카페", "디저트")
//
//            coVerify { storeService.getStoreDetail(storeId) }
//        }
//    }
//})