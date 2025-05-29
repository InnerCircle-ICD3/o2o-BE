package com.eatngo.product.event

import com.eatngo.common.exception.StockException.StockEmpty
import com.eatngo.common.exception.StockException.StockNotFound
import com.eatngo.inventory.event.StockEventPublisher
import com.eatngo.order.event.CreateOrderEvent
import com.eatngo.inventory.dto.StockDto
import com.eatngo.product.infra.ProductCachePersistence
import com.eatngo.product.infra.ProductPersistence
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ProductEventListener(
    private val productCachePersistence: ProductCachePersistence,
    private val productPersistence: ProductPersistence,
    private val stockEventPublisher: StockEventPublisher
) {

    @TransactionalEventListener(phase = AFTER_COMMIT)
    fun handleOrderEvent(event: CreateOrderEvent) {
        val decreasedStock: Int = decreaseStock(event.productId, event.orderId)
        publishEventToOrder(decreasedStock, event)
    }

    private fun decreaseStock(productId: Long, orderId: Long): Int {
        val decreasedStock: Int = try {
            productCachePersistence.decreaseStock(productId, 1)
        } catch (e: StockNotFound) {
            handleSoldOutEvent(orderId, productId); -1
        } catch (e: StockEmpty) {
            handleSoldOutEvent(orderId, productId); -1
        }
        return decreasedStock
    }

    private fun publishEventToOrder(decreasedStock: Int, event: CreateOrderEvent) {
        when (decreasedStock) {
            0 -> {
                productPersistence.updateStock(event.productId, 0)
                handleSoldOutEvent(event.orderId, event.productId)
            }
            -1 -> return
            else -> {
                stockEventPublisher.publishDecreaseEvent(
                    StockDto(event.orderId, event.productId, decreasedStock)
                )
            }
        }
    }

    private fun handleSoldOutEvent(orderId: Long, productId: Long) {
        stockEventPublisher.publishSoldOutEvent(
            StockDto(orderId, productId, 0)
        )
    }

}