package com.eatngo.store.usecase

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.domain.Store
import com.eatngo.store.event.publisher.StoreEventPublisher
import com.eatngo.store.service.StoreService
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class StoreStatusAutoChangeUseCaseTest {

    @MockK
    private lateinit var storeService: StoreService

    @MockK
    private lateinit var storeEventPublisher: StoreEventPublisher

    @InjectMockKs
    private lateinit var useCase: StoreStatusAutoChangeUseCase

    private val storeId = 1L
    private val mockStore = mockk<Store>(relaxed = true)
    private val updatedMockStore = mockk<Store>(relaxed = true)

    @BeforeEach
    fun setup() {
        clearAllMocks()
        
        // 기본 Mock 설정
        every { mockStore.status } returns StoreEnum.StoreStatus.OPEN
        every { updatedMockStore.status } returns StoreEnum.StoreStatus.CLOSED
    }

    @Test
    fun `재고 소진 시 매장 상태가 변경되고 이벤트가 발행된다`() {
        // given
        every { storeService.getStoreById(storeId) } returns mockStore
        every { storeService.updateStoreStatus(storeId, false) } returns updatedMockStore
        every { storeEventPublisher.publishStoreInventoryChanged(any(), any()) } just runs
        every { storeEventPublisher.publishStoreStatusChanged(any(), any(), any()) } just runs

        // when
        val result = useCase.changeStatusByInventory(storeId, hasStock = false)

        // then
        verify { storeService.getStoreById(storeId) }
        verify { storeService.updateStoreStatus(storeId, false) }
        verify { storeEventPublisher.publishStoreInventoryChanged(updatedMockStore, false) }
        verify { storeEventPublisher.publishStoreStatusChanged(updatedMockStore, any(), mockStore.status) }
        
        // 결과 확인
        assertEquals(updatedMockStore.status.name, result.status)
    }

    @Test
    fun `재고 복구 시 매장 상태가 변경되고 이벤트가 발행된다`() {
        // given
        every { mockStore.status } returns StoreEnum.StoreStatus.CLOSED
        every { updatedMockStore.status } returns StoreEnum.StoreStatus.OPEN
        every { storeService.getStoreById(storeId) } returns mockStore
        every { storeService.updateStoreStatus(storeId, true) } returns updatedMockStore
        every { storeEventPublisher.publishStoreInventoryChanged(any(), any()) } just runs
        every { storeEventPublisher.publishStoreStatusChanged(any(), any(), any()) } just runs

        // when
        val result = useCase.changeStatusByInventory(storeId, hasStock = true)

        // then
        verify { storeService.getStoreById(storeId) }
        verify { storeService.updateStoreStatus(storeId, true) }
        verify { storeEventPublisher.publishStoreInventoryChanged(updatedMockStore, true) }
        verify { storeEventPublisher.publishStoreStatusChanged(updatedMockStore, any(), mockStore.status) }
        
        // 결과 확인
        assertEquals(updatedMockStore.status.name, result.status)
    }

    @Test
    fun `매장 상태가 변경되지 않으면 상태 변경 이벤트는 발행되지 않는다`() {
        // given
        every { updatedMockStore.status } returns StoreEnum.StoreStatus.OPEN // 상태 변경 없음
        every { storeService.getStoreById(storeId) } returns mockStore
        every { storeService.updateStoreStatus(storeId, true) } returns updatedMockStore
        every { storeEventPublisher.publishStoreInventoryChanged(any(), any()) } just runs

        // when
        useCase.changeStatusByInventory(storeId, hasStock = true)

        // then
        verify { storeService.getStoreById(storeId) }
        verify { storeService.updateStoreStatus(storeId, true) }
        verify { storeEventPublisher.publishStoreInventoryChanged(updatedMockStore, true) }
        verify(exactly = 0) { storeEventPublisher.publishStoreStatusChanged(any(), any(), any()) }
    }
} 