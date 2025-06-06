package com.eatngo.store.event

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.domain.Store
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.Logger

@ExtendWith(MockKExtension::class)
class StoreEventListenerTest {

    @InjectMockKs
    private lateinit var listener: StoreEventListener

    private val mockStore = mockk<Store>(relaxed = true)
    private val userId = 1L
    private val storeId = 10L
    private val mockLogger = mockk<Logger>(relaxed = true)

    @BeforeEach
    fun setup() {
        clearAllMocks()
        
        mockkStatic(StoreEventListener.Companion::class)
        every { StoreEventListener.log } returns mockLogger
        
        every { mockStore.id } returns storeId
    }

    @Test
    fun `CUD 이벤트 - 생성 이벤트 처리`() {
        // given
        val event = StoreCUDEvent(
            store = mockStore,
            userId = userId,
            eventType = StoreCUDEventType.CREATED
        )

        // when
        listener.handleStoreCUDEvent(event)

        // then
        verify { mockLogger.info(any(), eq(storeId), eq(userId)) }
    }

    @Test
    fun `CUD 이벤트 - 업데이트 이벤트 처리`() {
        // given
        val event = StoreCUDEvent(
            store = mockStore,
            userId = userId,
            eventType = StoreCUDEventType.UPDATED
        )

        // when
        listener.handleStoreCUDEvent(event)

        // then
        verify { mockLogger.info(any(), eq(storeId), eq(userId)) }
    }

    @Test
    fun `삭제 이벤트 처리`() {
        // given
        val event = StoreDeletedEvent(
            storeId = storeId,
            userId = userId
        )

        // when
        listener.handleStoreDeletedEvent(event)

        // then
        verify { mockLogger.info(any(), eq(storeId), eq(userId)) }
    }

    @Test
    fun `상태 변경 이벤트 처리`() {
        // given
        val previousStatus = StoreEnum.StoreStatus.OPEN
        val currentStatus = StoreEnum.StoreStatus.CLOSED
        val event = StoreStatusChangedEvent(
            store = mockStore,
            userId = userId,
            previousStatus = previousStatus,
            currentStatus = currentStatus
        )

        // when
        listener.handleStoreStatusChangedEvent(event)

        // then
        verify { mockLogger.info(any(), eq(storeId), eq(userId), eq(previousStatus), eq(currentStatus)) }
    }

    @Test
    fun `재고 상태 변경 이벤트 - 재고 소진 처리`() {
        // given
        val event = StoreInventoryChangedEvent(
            store = mockStore,
            hasStock = false
        )

        // when
        listener.handleStoreInventoryChangedEvent(event)

        // then
        verify { mockLogger.info(any(), eq(storeId), eq(false)) }
        verify { mockLogger.info(any(), eq(storeId), eq("재고 소진")) }
    }

    @Test
    fun `재고 상태 변경 이벤트 - 재고 복구 처리`() {
        // given
        val event = StoreInventoryChangedEvent(
            store = mockStore,
            hasStock = true
        )

        // when
        listener.handleStoreInventoryChangedEvent(event)

        // then
        verify { mockLogger.info(any(), eq(storeId), eq(true)) }
        verify { mockLogger.info(any(), eq(storeId), eq("재고 복구")) }
    }
    
    companion object {
        private val log = mockk<Logger>()
    }
} 