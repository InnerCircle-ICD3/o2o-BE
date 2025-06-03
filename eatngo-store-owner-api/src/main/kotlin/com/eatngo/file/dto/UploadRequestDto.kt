package com.eatngo.file.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class UploadRequestDto(

    @field:NotBlank(message = "파일 이름은 필수입니다.")
    val fileName: String,

    @field:NotBlank(message = "컨텐츠 타입은 필수입니다.")
    val contentType: String,

    // S3 버킷 내에 파일을 저장할 폴더 경로
    // ex. "product", "profile", "store"
    @field:NotBlank(message = "저장 경로는 필수입니다.")
    val folderPath: String
)

data class UploadBatchRequestDto(
    @field:NotEmpty(message = "파일 목록은 비어있을 수 없습니다.")
    @field:Size(max = 3, message = "한 번에 최대 3개 파일까지 업로드 가능합니다.")
    val files: List<UploadRequestDto>
)
