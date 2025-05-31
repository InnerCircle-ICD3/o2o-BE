package com.eatngo.order.persistence

import com.eatngo.TestApplication
import com.eatngo.customer.domain.Customer
import com.eatngo.order.domain.Order
import com.eatngo.order.domain.OrderItem
import com.eatngo.order.domain.Status
import com.eatngo.order.infra.OrderPersistence
import com.eatngo.user_account.domain.UserAccount
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Tag("integration")
@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
@Transactional
class OrderPersistenceImplIntegrationTest(
    @Autowired private val orderPersistence: OrderPersistence,
) : BehaviorSpec() {
    init {
        this.given(name = "domain 객체가 주어지면") {
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
            this.`when`(name = "OrderPersistence에 넘겼을 때") {
                val saved = orderPersistence.save(domainOrder)
                this.then(name = "JPA를 통해 저장되고 도메인 객체로 반환된다") {
                    saved.id shouldBeGreaterThan 0L
                    saved.customerId shouldBe 1L
                    saved.orderItems.size shouldBe 1
                }
            }
        }

        this.given(name = "DB에 저장되어 있는 객체가 수정되었을 때") {
            val savedOrder = orderPersistence.save(
                Order.create(
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
            )
            this.`when`("이를 update 했을 때") {
                savedOrder.toCancel(
                    Customer(
                        id = 1L,
                        account = UserAccount(id = 1L, email = null),
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now(),
                    )
                )

                val updatedOrder = orderPersistence.save(savedOrder)

                this.then("JPA를 통해 저장되고 도메인 객체로 반환된다.") {
                    updatedOrder.id shouldBeGreaterThan 0L
                    updatedOrder.customerId shouldBe 1L
                    updatedOrder.orderItems.size shouldBe 1
                    updatedOrder.status shouldBe Status.CANCELED
                }
            }
        }
    }
}
