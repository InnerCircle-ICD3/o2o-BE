package com.eatngo.inventory.event

data class InventoryChangedEvent(
    val productId: Long,
    val inventoryChangedType: InventoryChangedType
)

enum class InventoryChangedType(
    private val value: String
) {
    OUT_OF_STOCK("재고 소진"),
    LOW_STOCK("재고 마감 임박"),
    IN_STOCK("재고 충분"),
    RESTOCKED("재고 소진 상태에서 다시 입고됨");
}