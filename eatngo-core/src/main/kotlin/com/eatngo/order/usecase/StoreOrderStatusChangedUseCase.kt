package com.eatngo.order.usecase

import com.eatngo.order.domain.Status
import com.eatngo.order.dto.OrderStatusChangedDto
import com.eatngo.order.service.OrderService
import com.eatngo.store_owner.service.StoreOwnerService
import org.springframework.stereotype.Service

@Service
class StoreOrderStatusChangedUseCase(
    private val orderService: OrderService,
    private val storeService: StoreOwnerService
) {
    fun change(dto: OrderStatusChangedDto){
        val store = storeService.getStoreOwnerById(dto.userId)
        val order = orderService.findById(dto.orderId)

        when (dto.status) {
            Status.CANCELED -> order.toCancel(store)
            Status.CONFIRMED -> order.toConfirm(store)
            else -> {  throw IllegalStateException("Cannot '${dto.status}' when status is '${order.status}'")}
        }

        orderService.update(order)
    }
}