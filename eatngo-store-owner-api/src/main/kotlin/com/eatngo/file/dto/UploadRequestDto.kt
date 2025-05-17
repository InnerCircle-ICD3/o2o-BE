package com.eatngo.file.dto

import jakarta.validation.constraints.NotBlank

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
