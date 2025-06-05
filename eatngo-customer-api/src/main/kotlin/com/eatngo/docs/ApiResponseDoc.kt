package com.eatngo.docs

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "API 성공 응답 예시")
abstract class ApiResponseSuccessDoc<T>(
    @Schema(description = "요청 성공 여부", example = "true")
    open val success: Boolean = true,
    @Schema(description = "응답 데이터")
    open val data: T
)

@Schema(description = "API 에러 응답 예시")
data class ApiResponseErrorDoc(
    @Schema(description = "요청 성공 여부", example = "false")
    val success: Boolean = false,
    @Schema(description = "에러 코드", example = "C001, C002... 공통 에러코드 정의 문서 확인")
    val errorCode: String,
    @Schema(description = "에러 코드에 정의된 에러 메시지, 따로 정의된 메세지가 없는 경우 서버의 e 메세지를 반환합니다.", example = "정의된 코드가 있는 경우: 매장 정보가 없습니다 / 정의된 코드가 없는 경우: JSON Parse Error...")
    val errorMessage: String
)
