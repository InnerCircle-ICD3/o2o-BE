package com.eatngo.subscription.service.impl

import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.exception.store.StoreException
import com.eatngo.common.exception.subscription.SubscriptionException
import com.eatngo.common.response.Cursor
import com.eatngo.extension.orThrow
import com.eatngo.inventory.infra.InventoryPersistence
import com.eatngo.product.infra.ProductPersistence
import com.eatngo.store.infra.StorePersistence
import com.eatngo.subscription.domain.StoreSubscription
import com.eatngo.subscription.dto.CustomerSubscriptionQueryParamDto
import com.eatngo.subscription.dto.StoreOwnerSubscriptionQueryParamDto
import com.eatngo.subscription.dto.StoreSubscriptionDto
import com.eatngo.subscription.dto.StoreSubscriptionQueryParamDto
import com.eatngo.subscription.infra.StoreSubscriptionPersistence
import com.eatngo.subscription.service.StoreSubscriptionService
import com.eatngo.store.service.StoreTotalStockService
import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * 상점 구독 서비스 구현체
 */
@Service
class StoreSubscriptionServiceImpl(
    private val storeSubscriptionPersistence: StoreSubscriptionPersistence,
    private val storePersistence: StorePersistence,
    private val productPersistence: ProductPersistence,
    private val storeTotalStockService: StoreTotalStockService
) : StoreSubscriptionService {

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

    override fun getSubscriptionsByQueryParameter(queryParam: StoreSubscriptionQueryParamDto): Cursor<StoreSubscriptionDto> {
        // 권한 검증
        when (queryParam) {
            is StoreOwnerSubscriptionQueryParamDto -> {
                val store = storePersistence.findById(queryParam.storeId).orThrow { StoreException.StoreNotFound(queryParam.storeId) }
                store.requireOwner(queryParam.storeOwnerId)
            }
        }

        val cursoredSubscriptions = storeSubscriptionPersistence.findAllByQueryParameter(queryParam)
        val storeIds = cursoredSubscriptions.contents.map { it.storeId }.distinct()
        
        val stores = storePersistence.findAllByIds(storeIds).associateBy { it.id }
        val cheapestProductInfoMap = getCheapestProductInfoBatch(storeIds)
        
        val subscriptionDtos = when (queryParam) {
            is CustomerSubscriptionQueryParamDto -> {
                val stockMap = storeTotalStockService.getStoreStockMapForResponse(storeIds)
                
                cursoredSubscriptions.contents.map { subscription ->
                    val store = stores[subscription.storeId].orThrow { StoreException.StoreNotFound(subscription.storeId) }
                    val today = LocalDate.now().dayOfWeek
                    val todayHour = store.businessHours?.find { it.dayOfWeek == today }
                    val totalStockCount = stockMap[subscription.storeId] ?: -1
                    val cheapestProductInfo = cheapestProductInfoMap[subscription.storeId] ?: getDefaultProductInfo()
                    
                    StoreSubscriptionDto.from(
                        subscription = subscription,
                        storeName = store.name.value,
                        description = store.description?.value,
                        mainImageUrl = store.imageUrl,
                        foodCategory = store.storeCategoryInfo.foodCategory?.map { it.value },
                        status = store.status,
                        discountRate = cheapestProductInfo.discountRate,
                        originalPrice = cheapestProductInfo.originalPrice,
                        discountedPrice = cheapestProductInfo.finalPrice,
                        todayPickupStartTime = todayHour?.openTime,
                        todayPickupEndTime = todayHour?.closeTime,
                        totalStockCount = totalStockCount,
                        pickupDay = store.pickUpDay.pickUpDay.name
                    )
                }
            }
            is StoreOwnerSubscriptionQueryParamDto -> {
                cursoredSubscriptions.contents.map { subscription ->
                    val store = stores[subscription.storeId].orThrow { StoreException.StoreNotFound(subscription.storeId) }
                    StoreSubscriptionDto.from(
                        subscription = subscription,
                        storeName = store.name.value,
                        mainImageUrl = store.imageUrl,
                        status = store.status
                    )
                }
            }
            else -> throw IllegalArgumentException("지원하지 않는 쿼리 파라미터 타입입니다")
        }

        return Cursor.from(subscriptionDtos, cursoredSubscriptions.lastId)
    }

    override fun isSubscribed(storeId: Long, customerId: Long?): Boolean {
        if (customerId == null) return false
        val subscription = storeSubscriptionPersistence.findAllByUserIdAndStoreId(customerId, storeId)
        return subscription?.isActive() == true
    }

    override fun getSubscribedStoreIds(customerId: Long): List<Long> {
        // Repository에서 이미 deletedAt IS NULL 조건으로 활성 구독만 조회
        return storeSubscriptionPersistence.findStoreIdsByUserId(customerId)
    }
    
    /**
     * 단일 매장 최저가 상품 정보 조회
     */
    private fun getCheapestProductInfo(storeId: Long): ProductInfo {
        val products = productPersistence.findAllActivatedProductByStoreId(storeId)
        return products.minByOrNull { it.price.finalPrice }
            ?.let { ProductInfo(it.price.originalPrice, it.price.discountRate, it.price.finalPrice) }
            ?: getDefaultProductInfo()
    }
    
    /**
     * 최저가 상품 정보 배치 조회
     */
    private fun getCheapestProductInfoBatch(storeIds: List<Long>): Map<Long, ProductInfo> {
        val allProducts = productPersistence.findAllActivatedProductsByStoreIds(storeIds)
        return allProducts.groupBy { it.storeId }
            .mapValues { (_, products) ->
                products.minByOrNull { it.price.finalPrice }
                    ?.let { ProductInfo(it.price.originalPrice, it.price.discountRate, it.price.finalPrice) }
                    ?: getDefaultProductInfo()
            }
    }
    
    /**
     * 기본 상품 정보 (상품이 없는 경우)
     */
    private fun getDefaultProductInfo(): ProductInfo {
        return ProductInfo(
            originalPrice = 0,
            discountRate = 0.0,
            finalPrice = 0
        )
    }

    private data class ProductInfo(
        val originalPrice: Int,
        val discountRate: Double,
        val finalPrice: Int
    )
}