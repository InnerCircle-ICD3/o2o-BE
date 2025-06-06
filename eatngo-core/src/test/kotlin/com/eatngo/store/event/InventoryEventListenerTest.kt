package com.eatngo.store.event

import com.eatngo.common.exception.product.ProductException
import com.eatngo.inventory.event.InventoryChangedEvent
import com.eatngo.inventory.event.InventoryChangedType
import com.eatngo.product.infra.ProductPersistence
import com.eatngo.product.infra.entity.ProductJpaEntity
import com.eatngo.store.dto.StoreDto
import com.eatngo.store.usecase.StoreStatusAutoChangeUseCase
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Optional

@ExtendWith(MockKExtension::class)
class InventoryEventListenerTest {

    @MockK
    private lateinit var storeStatusAutoChangeUseCase: StoreStatusAutoChangeUseCase

    @MockK
    private lateinit var productPersistence: ProductPersistence

    @InjectMockKs
    private lateinit var listener: InventoryEventListener

    private val productId = 1L
    private val storeId = 10L
    private val mockProductEntity = mockk<ProductJpaEntity>()
    private val mockStoreDto = mockk<StoreDto>()

    @BeforeEach
    fun setup() {
        clearAllMocks()
        
        // 기본 Mock 설정
        every { mockProductEntity.storeId } returns storeId
        every { productPersistence.findActivatedProductById(productId) } returns Optional.of(mockProductEntity)
        every { storeStatusAutoChangeUseCase.changeStatusByInventory(any(), any()) } returns mockStoreDto
    }

    @Test
    fun `OUT_OF_STOCK 이벤트를 받으면 매장 상태를 재고 없음으로 변경한다`() {
        // given
        val event = InventoryChangedEvent(productId, InventoryChangedType.OUT_OF_STOCK)

        // when
        listener.handleInventoryChangedEvent(event)

        // then
        verify { productPersistence.findActivatedProductById(productId) }
        verify { storeStatusAutoChangeUseCase.changeStatusByInventory(storeId, false) }
    }

    @Test
    fun `RESTOCKED 이벤트를 받으면 매장 상태를 재고 있음으로 변경한다`() {
        // given
        val event = InventoryChangedEvent(productId, InventoryChangedType.RESTOCKED)

        // when
        listener.handleInventoryChangedEvent(event)

        // then
        verify { productPersistence.findActivatedProductById(productId) }
        verify { storeStatusAutoChangeUseCase.changeStatusByInventory(storeId, true) }
    }

    @Test
    fun `기타 이벤트 타입은 매장 상태를 변경하지 않는다`() {
        // given
        val event = InventoryChangedEvent(productId, InventoryChangedType.LOW_STOCK)

        // when
        listener.handleInventoryChangedEvent(event)

        // then
        verify(exactly = 0) { productPersistence.findActivatedProductById(any()) }
        verify(exactly = 0) { storeStatusAutoChangeUseCase.changeStatusByInventory(any(), any()) }
    }

    @Test
    fun `제품이 존재하지 않을 경우 예외를 발생시킨다`() {
        // given
        val event = InventoryChangedEvent(productId, InventoryChangedType.OUT_OF_STOCK)
        every { productPersistence.findActivatedProductById(productId) } returns Optional.empty()

        // when/then
        io.mockk.verify {
            runCatching { listener.handleInventoryChangedEvent(event) }
                .onFailure { assert(it is ProductException.ProductNotFound) }
        }
    }
} 