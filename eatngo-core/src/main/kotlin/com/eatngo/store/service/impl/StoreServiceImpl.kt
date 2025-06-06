package com.eatngo.store.service.impl

import com.eatngo.common.constant.StoreEnum
import com.eatngo.common.exception.FileException
import com.eatngo.common.exception.store.StoreException
import com.eatngo.extension.orThrow
import com.eatngo.file.FileStorageService
import com.eatngo.store.domain.Store
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
    private val fileStorageService: FileStorageService
) : StoreService {

    override fun createStore(request: StoreCreateDto): Store {
        val store = Store.create(request)

        store.apply {
            imageUrl = request.imageUrl?.let { fileStorageService.resolveImageUrl(it)
                .orThrow { FileException.ImageUrlResolveFailed(request.imageUrl) } }
        }
        return storePersistence.save(store)
    }

    override fun updateStore(id: Long, request: StoreUpdateDto): Store {
        val existingStore = storePersistence.findById(id).orThrow { StoreException.StoreNotFound(id) }
        existingStore.requireOwner(request.storeOwnerId)
        existingStore.update(request)
        if (request.mainImageUrl != null) {
            existingStore.imageUrl =
                fileStorageService.resolveImageUrl(request.mainImageUrl)
                    .orThrow { FileException.ImageUrlResolveFailed(request.mainImageUrl) }
        }
        return storePersistence.save(existingStore)
    }

    override fun updateStoreStatus(id: Long, newStatus: StoreEnum.StoreStatus, storeOwnerId: Long?): Store {
        val existingStore = storePersistence.findById(id).orThrow { StoreException.StoreNotFound(id) }
        // 점주가 직접 변경하는 경우에만 권한 확인
        storeOwnerId?.let { existingStore.requireOwner(it) }

        when (newStatus) {
            StoreEnum.StoreStatus.OPEN -> existingStore.toOpen()
            StoreEnum.StoreStatus.CLOSED -> existingStore.toClose()
            StoreEnum.StoreStatus.PENDING -> existingStore.toPending()
        }

        val updateSuccess = storePersistence.updateStatus(id, newStatus)
        if (!updateSuccess) {
            throw StoreException.StoreNotFound(id)
        }

        return storePersistence.findById(id).orThrow { StoreException.StoreNotFound(id) }
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