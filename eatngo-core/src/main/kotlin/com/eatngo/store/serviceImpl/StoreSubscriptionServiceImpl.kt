package com.eatngo.store.serviceImpl

import com.eatngo.common.exception.StoreException
import com.eatngo.store.domain.StoreSubscription
import com.eatngo.store.dto.StoreSubscriptionDto
import com.eatngo.store.dto.extension.toDto
import com.eatngo.store.infra.StorePersistence
import com.eatngo.store.infra.StoreSubscriptionPersistence
import com.eatngo.store.service.StoreSubscriptionService
import org.springframework.stereotype.Service

/**
 * 상점 구독 서비스 구현체
 */
@Service
class StoreSubscriptionServiceImpl(
    private val storeSubscriptionPersistence: StoreSubscriptionPersistence,
    private val storePersistence: StorePersistence
) : StoreSubscriptionService {
    // 임시 상품 정보 (할인율 10%, 원가 10,000원, 할인가 9,000원)
    private val tempProductInfo = object {
        val discountRate = 0.1
        val originalPrice = 10000
        val discountedPrice = 9000
    }

    override fun toggleSubscription(storeId: Long): StoreSubscriptionDto {
        val userId = 1L // TODO: Security Context에서 가져오기
        val userName = "임시사용자" // TODO: Security Context에서 가져오기

        val store = storePersistence.findById(storeId)
            ?: throw StoreException.StoreNotFound(storeId)

        val subscription = storeSubscriptionPersistence.findByUserIdAndStoreId(userId, storeId)?.let { existingSubscription ->
            if (existingSubscription.deletedAt != null) {
                // 재구독
                existingSubscription.restore()
                storeSubscriptionPersistence.save(existingSubscription)
            } else {
                // 구독 해제
                existingSubscription.softDelete()
                storeSubscriptionPersistence.save(existingSubscription)
            }
            existingSubscription
        } ?: run {
            // 신규 구독
            val newSubscription = StoreSubscription(userId = userId, storeId = storeId)
            storeSubscriptionPersistence.save(newSubscription)
        }

        return subscription.toDto(
            userName = userName,
            storeName = store.name.value,
            mainImageUrl = store.imageUrl?.value,
            status = store.status,
            discountRate = tempProductInfo.discountRate,
            originalPrice = tempProductInfo.originalPrice,
            discountedPrice = tempProductInfo.discountedPrice
        )
    }

    override fun getSubscriptionById(id: Long): StoreSubscriptionDto {
        val userName = "임시사용자" // TODO: Security Context에서 가져오기
        val subscription = storeSubscriptionPersistence.findById(id)
            ?: throw StoreException.SubscriptionNotFound(id)
        val store = storePersistence.findById(subscription.storeId)
            ?: throw StoreException.StoreNotFound(subscription.storeId)

        // 하드코딩된 상품 정보 사용
        return subscription.toDto(
            userName = userName,
            storeName = store.name.value,
            mainImageUrl = store.imageUrl?.value,
            status = store.status,
            discountRate = tempProductInfo.discountRate,
            originalPrice = tempProductInfo.originalPrice,
            discountedPrice = tempProductInfo.discountedPrice
        )
    }

    override fun getMySubscriptions(): List<StoreSubscriptionDto> {
        val userId = 1L
        val userName = "임시사용자" // TODO: Security Context에서 가져오기
        val subscriptions = storeSubscriptionPersistence.findByUserId(userId)
        val storeIds = subscriptions.map { it.storeId }
        val stores = storePersistence.findAllByIds(storeIds).associateBy { it.id }

        return subscriptions.map { subscription ->
            val store = stores[subscription.storeId] ?: throw StoreException.StoreNotFound(subscription.storeId)
            subscription.toDto(
                userName = userName,
                storeName = store.name.value,
                mainImageUrl = store.imageUrl?.value,
                status = store.status,
                discountRate = tempProductInfo.discountRate,
                originalPrice = tempProductInfo.originalPrice,
                discountedPrice = tempProductInfo.discountedPrice
            )
        }
    }

    override fun getSubscriptionsByStoreId(storeId: Long): List<StoreSubscriptionDto> {
        val userName = "임시사용자" // TODO: Security Context에서 가져오기
        val store = storePersistence.findById(storeId)
            ?: throw StoreException.StoreNotFound(storeId)

        return storeSubscriptionPersistence.findByStoreId(storeId)
            .map { subscription ->
                subscription.toDto(
                    userName = userName,
                    storeName = store.name.value,
                    mainImageUrl = store.imageUrl?.value,
                    status = store.status,
                    discountRate = tempProductInfo.discountRate,
                    originalPrice = tempProductInfo.originalPrice,
                    discountedPrice = tempProductInfo.discountedPrice
                )
            }
    }
//    override fun toggleSubscription(storeId: Long): StoreSubscriptionDto {
//        val userId = "임시사용자" // TODO: Security Context에서 가져오기
//
//        // 매장 정보 조회
//        val store = storePersistence.findById(storeId) ?: throw StoreException.StoreNotFound(storeId)
//        val productInfo = productPersistence.findByStoreId(storeId)
//            ?: throw StoreException.StoreNotAvailable(storeId)
//
//        val existingSubscription = storeSubscriptionPersistence.findByUserIdAndStoreId(userId, storeId)
//
//        val subscription = if (existingSubscription != null) {
//            existingSubscription.softDelete()
//            storeSubscriptionPersistence.save(existingSubscription)
//        } else {
//            val newSubscription = StoreSubscription(userId = userId, storeId = storeId)
//            storeSubscriptionPersistence.save(newSubscription)
//        }
//
//        return subscription.toDto(
//            storeName = store.name,
//            mainImageUrl = store.imageUrl,
//            status = store.status.name,
//            discountRate = productInfo.discountRate,
//            originalPrice = productInfo.originalPrice,
//            discountedPrice = productInfo.discountedPrice
//        )
//    }
//
//    override fun getSubscriptionById(id: String): StoreSubscriptionDto {
//        val subscription = storeSubscriptionPersistence.findById(id)
//            ?: throw StoreException.SubscriptionNotFound(id)
//        val store = storePersistence.findById(subscription.storeId)
//            ?: throw StoreException.StoreNotFound(subscription.storeId)
//        val productInfo = productPersistence.findByStoreId(store.id)
//            ?: throw StoreException.StoreNotAvailable(store.id)
//
//        return subscription.toDto(
//            storeName = store.name,
//            mainImageUrl = store.imageUrl,
//            status = store.status.name,
//            discountRate = productInfo.discountRate,
//            originalPrice = productInfo.originalPrice,
//            discountedPrice = productInfo.discountedPrice
//        )
//    }
//
//    override fun getMySubscriptions(): List<StoreSubscriptionDto> {
//        val userId = "임시사용자"
//        val subscriptions = storeSubscriptionPersistence.findByUserId(userId)
//        return subscriptions.mapNotNull { subscription ->
//            val store = storePersistence.findById(subscription.storeId) ?: return@mapNotNull null
//            val productInfo = productPersistence.findByStoreId(store.id) ?: return@mapNotNull null
//
//            subscription.toDto(
//                storeName = store.name,
//                mainImageUrl = store.imageUrl,
//                status = store.status.name,
//                discountRate = productInfo.discountRate,
//                originalPrice = productInfo.originalPrice,
//                discountedPrice = productInfo.discountedPrice
//            )
//        }
//    }
//
//    override fun getSubscriptionsByStoreId(storeId: Long): List<StoreSubscriptionDto> {
//        val subscriptions = storeSubscriptionPersistence.findByStoreId(storeId)
//        val store = storePersistence.findById(storeId) ?: throw StoreException.StoreNotFound(storeId)
//        val productInfo = productPersistence.findByStoreId(store.id)
//            ?: throw StoreException.StoreNotAvailable(store.id)
//
//        return subscriptions.map { subscription ->
//            subscription.toDto(
//                storeName = store.name,
//                mainImageUrl = store.imageUrl,
//                status = store.status.name,
//                discountRate = productInfo.discountRate,
//                originalPrice = productInfo.originalPrice,
//                discountedPrice = productInfo.discountedPrice
//            )
//        }
//    }

}