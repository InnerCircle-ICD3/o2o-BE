package com.eatngo.order.persistence

import com.eatngo.order.domain.Order
import com.eatngo.order.domain.OrderItem
import com.eatngo.order.domain.Status
import com.eatngo.order.rdb.entity.OrderItemJpaEntity
import com.eatngo.order.rdb.entity.OrderJpaEntity
import com.eatngo.order.rdb.repository.OrderRdbRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class OrderPersistenceImplUnitTest : StringSpec({
    "save() 는 리포지토리에 위임하고, 리턴된 엔티티의 id 를 도메인으로 매핑해야 한다" {
        val mockRepo = mockk<OrderRdbRepository>()
        val persistence = OrderPersistenceImpl(mockRepo)

        val domainOrder = Order.create(
            customerId = 1L,
            storeId = 1L,
            orderNumber = 99L,
            orderItems = listOf(
                OrderItem.of(
                    productId = 42L,
                    name = "테스트상품",
                    quantity = 2,
                    price = 7_500
                )
            )
        )

        // 저장 시 리포지토리가 반환할 JPA 엔티티를 id=123L 로 세팅해 둠
        val returnedEntity = OrderJpaEntity(
            id = 1L,
            customerId = 1L,
            storeId = 1L,
            orderNumber = 99L,
            createdAt = domainOrder.createdAt,
            updatedAt = domainOrder.updatedAt,
            status = Status.CREATED
        )

        domainOrder.orderItems.forEach { orderItem: OrderItem ->
            returnedEntity.orderItems.add(
                OrderItemJpaEntity.from(orderItem, returnedEntity)
            )
        }

        every { mockRepo.save(any()) } returns returnedEntity

        val saved = persistence.save(domainOrder)

        saved.id shouldBe 1L

        verify(exactly = 1) {
            mockRepo.save(match { entity ->
                entity.customerId == domainOrder.customerId &&
                        entity.storeId == domainOrder.storeId &&
                        entity.orderNumber == domainOrder.orderNumber
            })
        }
    }
})
