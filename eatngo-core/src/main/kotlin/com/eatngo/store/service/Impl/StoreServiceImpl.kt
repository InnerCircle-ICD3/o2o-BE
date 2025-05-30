package com.eatngo.store.service.Impl

import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.exception.StoreException
import com.eatngo.extension.orThrow
import com.eatngo.store.domain.Store
import com.eatngo.store.dto.PickUpInfoDto
import com.eatngo.store.dto.StoreCreateDto
import com.eatngo.store.dto.StoreUpdateDto
import com.eatngo.store.infra.StorePersistence
import com.eatngo.store.service.StoreService
import org.springframework.stereotype.Service

/**
 * 상점 서비스 구현체
 */
@Service
class StoreServiceImpl(
    private val storePersistence: StorePersistence,
) : StoreService {

    override fun createStore(request: StoreCreateDto): Store {
        val store = Store.create(request)
        return storePersistence.save(store)
    }

    override fun updateStore(id: Long, request: StoreUpdateDto): Store {
        val existingStore = storePersistence.findById(id).orThrow { StoreException.StoreNotFound(id) }
        existingStore.requireOwner(request.storeOwnerId)
        existingStore.update(request)
        return storePersistence.save(existingStore)
    }

    override fun updateStoreStatus(id: Long, hasStock: Boolean): Store {
        val existingStore = storePersistence.findById(id).orThrow { StoreException.StoreNotFound(id) }
        existingStore.updateStoreStatus(hasStock)
        return storePersistence.save(existingStore)
    }

    override fun updateStoreStatus(id: Long, newStatus: String, storeOwnerId: Long): Store {
        val existingStore = storePersistence.findById(id).orThrow { StoreException.StoreNotFound(id) }
        existingStore.requireOwner(storeOwnerId)

        val status = try {
            StoreEnum.StoreStatus.valueOf(newStatus.trim().uppercase())
        } catch (e: IllegalArgumentException) {
            throw StoreException.StoreStatusInvalid(newStatus)
        }

        when (status) {
            StoreEnum.StoreStatus.OPEN -> existingStore.toOpen()
            StoreEnum.StoreStatus.CLOSED -> existingStore.toClose()
            StoreEnum.StoreStatus.PENDING -> existingStore.toPending()
        }

        return storePersistence.save(existingStore)
    }

    //TODO: UI 따라 삭제 가능성 있는 로직 -> 픽업 정보를 따로 변경하는 화면이 있지 않은 이상 매정 정보에서 같이 변경할거라 삭제가능성 있음
    override fun updateStorePickupInfo(id: Long, request: PickUpInfoDto, storeOwnerId: Long): Store {
        val existingStore = storePersistence.findById(id).orThrow { StoreException.StoreNotFound(id) }
        existingStore.requireOwner(storeOwnerId)

        existingStore.updatePickupInfo(
            request.pickupDay,
            request.pickupStartTime,
            request.pickupEndTime
        )
        return storePersistence.save(existingStore)
    }

    override fun deleteStore(id: Long, storeOwnerId: Long): Boolean {
        val existingStore = storePersistence.findById(id).orThrow { StoreException.StoreNotFound(id) }
        existingStore.requireOwner(storeOwnerId)

        return storePersistence.deleteById(id)
    }

    override fun getStoresByStoreOwnerId(storeOwnerId: Long): List<Store> {
        val existingStore = storePersistence.findByOwnerId(storeOwnerId)
        return existingStore
    }

    override fun getStoreById(id: Long): Store {
        val existingStore = storePersistence.findById(id).orThrow { StoreException.StoreNotFound(id) }
        return existingStore
    }
}