package com.eatngo.order.persistence

import com.eatngo.TestApplication
import com.eatngo.order.domain.Order
import com.eatngo.order.domain.OrderItem
import com.eatngo.order.infra.OrderPersistence
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles


@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
class OrderPersistenceImplIntegrationTest(
    @Autowired private val orderPersistence: OrderPersistence
) : StringSpec() {

    override fun extensions() = listOf(SpringExtension)

    init {
        "save() 는 JPA를 통해 저장되고 도메인 객체로 반환된다" {
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

            val saved = orderPersistence.save(domainOrder)

            saved.id shouldBeGreaterThan 0L
            saved.customerId shouldBe 1L
            saved.orderItems.size shouldBe 1
        }
    }
}
