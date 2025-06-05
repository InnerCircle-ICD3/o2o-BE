package com.eatngo.store.docs

import io.swagger.v3.oas.annotations.media.Schema


@Schema(description = "매장 상태 변경 요청을 위한 리퀘스트 모델")
interface StoreStatusUpdateRequestDocs{
    @get:Schema(
        description = "변경할 매장 상태",
        example = "OPEN",
        allowableValues = ["OPEN", "CLOSED", "PENDING"],
        required = true
    )
    val status: String
}
