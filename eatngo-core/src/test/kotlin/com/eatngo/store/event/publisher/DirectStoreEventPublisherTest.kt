package com.eatngo.store.event.publisher

import com.eatngo.common.constant.StoreEnum
import com.eatngo.store.domain.Store
import com.eatngo.store.event.StoreCUDEvent
import com.eatngo.store.event.StoreDeletedEvent
import com.eatngo.store.event.StoreInventoryChangedEvent
import com.eatngo.store.event.StoreStatusChangedEvent
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class DirectStoreEventPublisherTest {

    @MockK
    private lateinit var eventPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var publisher: DirectStoreEventPublisher

    private val userId = 1L
    private val storeId = 10L
    private val mockStore = mockk<Store>(relaxed = true)

    @BeforeEach
    fun setup() {
        clearAllMocks()
        
        // 기본 Mock 설정
        every { mockStore.id } returns storeId
        every { mockStore.createdAt } returns LocalDateTime.now()
        every { mockStore.updatedAt } returns LocalDateTime.now()
        every { mockStore.deletedAt } returns null
        every { mockStore.status } returns StoreEnum.StoreStatus.OPEN
        every { eventPublisher.publishEvent(any()) } just runs
    }

    @Test
    fun `매장 생성 이벤트가 올바르게 발행된다`() {
        // given
        every { mockStore.createdAt } returns LocalDateTime.now()
        every { mockStore.updatedAt } returns mockStore.createdAt

        // when
        publisher.publishStoreCreated(mockStore, userId)

        // then
        verify { eventPublisher.publishEvent(match { it is StoreCUDEvent }) }
    }

    @Test
    fun `매장 정보 업데이트 이벤트가 올바르게 발행된다`() {
        // when
        publisher.publishStoreUpdated(mockStore, userId)

        // then
        verify { eventPublisher.publishEvent(match { it is StoreCUDEvent }) }
    }

    @Test
    fun `매장 삭제 이벤트가 올바르게 발행된다`() {
        // when
        publisher.publishStoreDeleted(storeId, userId)

        // then
        verify { eventPublisher.publishEvent(match { it is StoreDeletedEvent }) }
    }

    @Test
    fun `매장 상태 변경 이벤트가 올바르게 발행된다`() {
        // given
        val previousStatus = StoreEnum.StoreStatus.CLOSED

        // when
        publisher.publishStoreStatusChanged(mockStore, userId, previousStatus)

        // then
        verify { eventPublisher.publishEvent(match { it is StoreStatusChangedEvent }) }
    }

    @Test
    fun `매장 재고 상태 변경 이벤트가 올바르게 발행된다`() {
        // when
        publisher.publishStoreInventoryChanged(mockStore, true)

        // then
        verify { eventPublisher.publishEvent(match { it is StoreInventoryChangedEvent && it.hasStock }) }
    }

    @Test
    fun `매장 재고 소진 이벤트가 올바르게 발행된다`() {
        // when
        publisher.publishStoreInventoryChanged(mockStore, false)

        // then
        verify { eventPublisher.publishEvent(match { it is StoreInventoryChangedEvent && !it.hasStock }) }
    }
} 