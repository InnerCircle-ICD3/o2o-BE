//package com.eatngo.store.controller
//
//import com.appmattus.kotlinfixture.kotlinFixture
//import com.eatngo.common.constant.StoreEnum
//import com.eatngo.common.response.ApiResponse
//import com.eatngo.store.dto.*
//import com.eatngo.store.service.StoreSubscriptionService
//import io.kotest.core.spec.style.DescribeSpec
//import io.kotest.matchers.shouldBe
//import io.kotest.matchers.types.shouldBeTypeOf
//import io.mockk.coEvery
//import io.mockk.coVerify
//import io.mockk.mockk
//import java.time.ZonedDateTime
//
//class StoreSubscriptionControllerTest : DescribeSpec({
//    val storeSubscriptionService = mockk<StoreSubscriptionService>()
//    val subscriptionController = StoreSubscriptionController(storeSubscriptionService)
//    val fixture = kotlinFixture()
//
//    // 테스트용 데이터 준비
//    val userId = "user123"
//    val subscriptionId = "subscription123"
//    val storeId: Long = 1
//
//    val subscriptionDto = StoreSubscriptionDto(
//        id = subscriptionId,
//        userId = userId,
//        storeId = storeId,
//        createdAt = ZonedDateTime.now(),
//        updatedAt = ZonedDateTime.now(),
//        deletedAt = null,
//        subscribed = true,
//        store = null
//    )
//
//    val storeSummary = StoreResponse(
//        storeId = storeId,
//        name = "테스트 매장",
//        mainImageUrl = "http://example.com/image.jpg",
//        status = StoreEnum.StoreStatus.OPEN,
//        isAvailableForPickup = true,
//        pickupAvailableForTomorrow = true
//    )
//
//    val subscriptionSummary = StoreSubscriptionSummary(
//        id = subscriptionId,
//        storeId = storeId,
//        userId = userId,
//        createdAt = ZonedDateTime.now(),
//        store = storeSummary
//    )
//
//    val toggleResponse = SubscriptionToggleResponse(
//        subscribed = true,
//        subscriptionId = subscriptionId,
//        store = StoreResponse(
//            id = storeId,
//            name = "테스트 매장",
//            imageUrl = "http://example.com/image.jpg"
//        )
//    )
//
//    val subscriptionWithStore = subscriptionDto.copy(
//        store = storeSummary
//    )
//
//    val customerSubscriptionResponse = CustomerStoreSubscriptionResponse(
//        subscriptionId = subscriptionId,
//        storeId = storeId,
//        store = storeSummary
//    )
//
//    val customerSubscriptionListResponse = CustomerStoreSubscriptionListResponse(
//        subscriptions = listOf(customerSubscriptionResponse),
//        totalCount = 1
//    )
//
//    describe("toggleSubscription") {
//        it("상점 구독을 토글한다") {
//            // Given
//            coEvery {
//                storeSubscriptionService.toggleSubscription(storeId)
//            } returns subscriptionWithStore
//
//            // When
//            val result = subscriptionController.toggleSubscription(storeId)
//
//            // Then
//            result.shouldBeTypeOf<ApiResponse.Success<SubscriptionToggleResponse>>()
//            result.data.subscribed shouldBe true
//            result.data.subscriptionId shouldBe subscriptionId
//
//            coVerify { storeSubscriptionService.toggleSubscription(storeId) }
//        }
//    }
//
//    describe("unsubscribeStore") {
//        it("상점 구독을 해제한다") {
//            // Given
//            coEvery { storeSubscriptionService.deleteSubscription(subscriptionId) } returns true
//
//            // When
//            val result = subscriptionController.unsubscribeStore(subscriptionId)
//
//            // Then
//            result.shouldBeTypeOf<ApiResponse.Success<Boolean>>()
//            result.data shouldBe true
//
//            coVerify { storeSubscriptionService.deleteSubscription(subscriptionId) }
//        }
//    }
//
//    describe("getMyStoreSubscriptions") {
//        it("내 구독 목록을 조회한다") {
//            // Given
//            coEvery {
//                storeSubscriptionService.getSubscriptionsByUserId("현재 로그인한 사용자 ID", 10, 0)
//            } returns listOf(subscriptionSummary)
//
//            // When
//            val result = subscriptionController.getMyStoreSubscriptions(10, 0)
//
//            // Then
//            result.shouldBeTypeOf<ApiResponse.Success<CustomerStoreSubscriptionListResponse>>()
//            result.data.subscriptions.size shouldBe 1
//            result.data.subscriptions[0].subscriptionId shouldBe subscriptionId
//            result.data.totalCount shouldBe 1
//
//            coVerify { storeSubscriptionService.getSubscriptionsByUserId("현재 로그인한 사용자 ID", 10, 0) }
//        }
//    }
//
//    describe("getSubscribedStores") {
//        it("내가 구독한 매장 목록을 조회한다") {
//            // Given
//            val userIdLong = 123L
//            val subscribedStores = listOf(storeSummary)
//
//            coEvery {
//                storeSubscriptionService.getMySubscribedStores(userIdLong)
//            } returns subscribedStores
//
//            // When
//            val result = subscriptionController.getSubscribedStores()
//
//            // Then
//            result.shouldBeTypeOf<ApiResponse.Success<List<StoreResponse>>>()
//            result.data.size shouldBe 1
//            result.data[0].storeId shouldBe storeId
//
//            coVerify { storeSubscriptionService.getMySubscribedStores(userIdLong) }
//        }
//    }
//
//    describe("getStoreSubscriptions") {
//        val subscriptionsList = listOf(subscriptionSummary)
//
//        it("특정 매장의 구독 목록을 조회한다") {
//            // Given
//            coEvery {
//                storeSubscriptionService.getSubscriptionsByStoreId(storeId, 10, 0)
//            } returns subscriptionsList
//
//            // When
//            val result = subscriptionController.getStoreSubscriptions(storeId, 10, 0)
//
//            // Then
//            result.shouldBeTypeOf<ApiResponse.Success<List<StoreSubscriptionSummary>>>()
//            result.data.size shouldBe 1
//            result.data[0].storeId shouldBe storeId
//
//            coVerify { storeSubscriptionService.getSubscriptionsByStoreId(storeId, 10, 0) }
//        }
//    }
//
//    describe("getStoreSubscription") {
//        it("구독 정보를 조회한다") {
//            // Given
//            coEvery {
//                storeSubscriptionService.getSubscriptionById(subscriptionId)
//            } returns subscriptionDto
//
//            // When
//            val result = subscriptionController.getStoreSubscription(subscriptionId)
//
//            // Then
//            result.shouldBeTypeOf<ApiResponse.Success<StoreSubscriptionDto>>()
//            result.data.id shouldBe subscriptionId
//            result.data.storeId shouldBe storeId
//
//            coVerify { storeSubscriptionService.getSubscriptionById(subscriptionId) }
//        }
//    }
//})