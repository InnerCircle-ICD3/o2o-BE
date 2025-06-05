package com.eatngo.subscription.docs

import com.eatngo.auth.annotation.CustomerId
import com.eatngo.common.response.ApiResponse
import com.eatngo.subscription.dto.StoreSubscriptionResponse
import com.eatngo.subscription.dto.SubscriptionToggleResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable

@Tag(name = "매장 구독", description = "매장 구독 관련 API")
interface StoreSubscriptionDocs {

    @Operation(
        summary = "매장 구독/구독 취소 토글",
        description = """
        고객이 특정 매장을 구독하거나, 이미 구독 중인 경우 구독을 취소합니다.
        한 번의 요청으로 구독 상태를 토글할 수 있으며,
        구독 시에는 매장의 새로운 상품, 이벤트 등의 알림을 받을 수 있습니다.
        이미 구독 중이라면 구독이 취소되고, 구독 중이 아니라면 구독이 등록됩니다.
    """
    )
    fun toggleSubscription(
        @Parameter(description = "구독할 매장 ID", required = true)
        @PathVariable storeId: Long,

        @Parameter(description = "구독하는 고객 ID", required = true)
        @CustomerId customerId: Long
    ): ApiResponse<SubscriptionToggleResponse>

    @Operation(
        summary = "구독 매장 목록 조회(내 구독 목록 조회)",
        description = "고객이 구독한 매장 목록을 조회합니다."
    )
    fun getMySubscriptions(
        @Parameter(description = "조회하는 고객 ID", required = true)
        @CustomerId customerId: Long
    ): ApiResponse<List<StoreSubscriptionResponse>>
}