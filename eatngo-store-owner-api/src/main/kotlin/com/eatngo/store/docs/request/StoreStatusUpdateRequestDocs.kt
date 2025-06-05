package com.eatngo.store.docs.request

import io.swagger.v3.oas.annotations.media.Schema


@Schema(description = "매장 상태 변경 요청을 위한 리퀘스트 모델")
interface StoreStatusUpdateRequestDocs{
    @get:Schema(
        description = "변경할 매장 상태(매장 상태는 생성 시 기본 PENDING이며, 점주는 OPEN, CLOSE만 가능)",
        example = "OPEN",
        allowableValues = ["OPEN", "CLOSED"],
        required = true
    )
    val status: String
}
