package com.eatngo.subscription.service.impl

import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.exception.store.StoreException
import com.eatngo.common.exception.subscription.SubscriptionException
import com.eatngo.extension.orThrow
import com.eatngo.product.infra.ProductPersistence
import com.eatngo.store.infra.StorePersistence
import com.eatngo.subscription.domain.StoreSubscription
import com.eatngo.subscription.dto.StoreSubscriptionDto
import com.eatngo.subscription.infra.StoreSubscriptionPersistence
import com.eatngo.subscription.service.StoreSubscriptionService
import org.springframework.stereotype.Service

/**
 * 상점 구독 서비스 구현체
 */
@Service
class StoreSubscriptionServiceImpl(
    private val storeSubscriptionPersistence: StoreSubscriptionPersistence,
    private val storePersistence: StorePersistence,
    private val productPersistence: ProductPersistence
) : StoreSubscriptionService {
    // 임시 상품 정보 (할인율 10%, 원가 10,000원, 할인가 9,000원)
    private val tempProductInfo = object {
        val discountRate = 0.1
        val originalPrice = 10000
        val discountedPrice = 9000
    }

    override fun toggleSubscription(storeId: Long, customerId: Long): Pair<StoreSubscriptionDto, StoreEnum.SubscriptionStatus> {
        val store = storePersistence.findById(storeId).orThrow { StoreException.StoreNotFound(storeId) }

        val subscription = storeSubscriptionPersistence.findAllByUserIdAndStoreId(customerId, storeId)
        val (resultSubscription, operationType) = if (subscription == null) {
            // 신규 구독 (INSERT)
            val created = storeSubscriptionPersistence.save(StoreSubscription.create(customerId, storeId))
            created to StoreEnum.SubscriptionStatus.CREATED
        } else {
            // 기존 row가 있으면 (UPDATE)
            val operationType = subscription.toggleOrRestore()
            val updated = storeSubscriptionPersistence.save(subscription)
            updated to operationType
        }

        val dto = StoreSubscriptionDto.from(
            subscription = resultSubscription,
            storeName = store.name.value,
            mainImageUrl = store.imageUrl,
            status = store.status
        )
        return dto to operationType
    }

    override fun getSubscriptionById(id: Long): StoreSubscriptionDto {
        val subscription = storeSubscriptionPersistence.findById(id).orThrow { SubscriptionException.SubscriptionNotFound(id) }
        val store = storePersistence.findById(subscription.storeId).orThrow { StoreException.StoreNotFound(subscription.storeId) }

        return StoreSubscriptionDto.from(
            subscription = subscription,
            storeName = store.name.value,
            mainImageUrl = store.imageUrl,
            status = store.status,
            discountRate = tempProductInfo.discountRate,
            originalPrice = tempProductInfo.originalPrice,
            discountedPrice = tempProductInfo.discountedPrice
        )
    }

    override fun getMySubscriptions(customerId: Long): List<StoreSubscriptionDto> {
        val subscriptions = storeSubscriptionPersistence.findByUserId(customerId)
        val storeIds = subscriptions.map { it.storeId }
        val stores = storePersistence.findAllByIds(storeIds).associateBy { it.id }

        return subscriptions.map { subscription ->
            val store = stores[subscription.storeId].orThrow {StoreException.StoreNotFound(subscription.storeId)}
            StoreSubscriptionDto.from(
                subscription = subscription,
                storeName = store.name.value,
                mainImageUrl = store.imageUrl,
                status = store.status,
                discountRate = tempProductInfo.discountRate,
                originalPrice = tempProductInfo.originalPrice,
                discountedPrice = tempProductInfo.discountedPrice
            )
        }
    }

    override fun getSubscriptionsByStoreId(storeId: Long, storeOwnerId: Long): List<StoreSubscriptionDto> {
        val store = storePersistence.findById(storeId).orThrow { StoreException.StoreNotFound(storeId) }
        store.requireOwner(storeOwnerId)

        return storeSubscriptionPersistence.findByStoreId(storeId)
            .map { subscription ->
                StoreSubscriptionDto.from(
                    subscription = subscription,
                    storeName = store.name.value,
                    mainImageUrl = store.imageUrl,
                    status = store.status,
                    discountRate = tempProductInfo.discountRate,
                    originalPrice = tempProductInfo.originalPrice,
                    discountedPrice = tempProductInfo.discountedPrice
                )
            }
    }
}