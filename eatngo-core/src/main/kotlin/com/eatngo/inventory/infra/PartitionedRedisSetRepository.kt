package com.eatngo.inventory.infra

/**
 * 파티셔닝된 Redis Set을 관리하는 리포지토리 인터페이스
 *
 * 대규모 트래픽 시 단일 Redis Set 키의 병목을 완화하기 위해
 * 여러 파티션으로 분할하여 관리합니다.
 */
interface PartitionedRedisSetRepository {
    /**
     * 지정된 파티션의 Set에 멤버를 추가합니다.
     */
    fun add(baseKey: String, productId: Long, partitionCount: Int)

    /**
     * 모든 파티션에서 멤버를 조회합니다.
     */
    fun members(baseKey: String, partitionCount: Int): Set<String>

    /**
     * 지정된 파티션의 Set에서 멤버를 제거합니다.
     */
    fun remove(baseKey: String, productId: Long, partitionCount: Int)

    /**
     * 모든 파티션의 Set을 삭제합니다.
     */
    fun deleteAll(baseKey: String, partitionCount: Int)

    /**
     * 특정 파티션의 멤버들을 조회하고 해당 파티션을 삭제합니다.
     */
    fun popAndDeletePartition(baseKey: String, partitionIndex: Int): Set<String>
}
