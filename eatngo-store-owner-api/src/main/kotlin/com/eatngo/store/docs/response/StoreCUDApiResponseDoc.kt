package com.eatngo.store.docs.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/** 컨트롤러에서 반환되는 응답 래핑 */

@Schema(description = "매장 CUD(등록/수정/삭제) 성공 응답 예시")
data class StoreCUDApiResponseDoc(
    @Schema(description = "요청 성공 여부", example = "true")
    val success: Boolean = true,
    @Schema(description = "응답 데이터")
    val data: StoreCUDResponseDoc = StoreCUDResponseDoc()
)

@Schema(description = "매장 CUD(등록/수정/삭제) 성공 응답 예시")
data class StoreCUDResponseDoc(
    @Schema(description = "매장 ID", example = "123")
    val storeId: Long = 123,
    @Schema(description = "작업 시각", example = "2025-06-05T17:00:00")
    val actionTime: LocalDateTime = LocalDateTime.of(2025, 6, 5, 17, 0, 0)
)