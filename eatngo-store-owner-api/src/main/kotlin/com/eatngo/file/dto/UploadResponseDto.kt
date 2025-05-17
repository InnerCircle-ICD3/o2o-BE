package com.eatngo.file.dto

data class UploadResponseDto(
    val preSignedUrl: String,
    val s3Key: String
) {
    companion object {
        fun from(preSignedUrl: Pair<String, String>): UploadResponseDto {
            return UploadResponseDto(
                preSignedUrl.first,
                preSignedUrl.second
            )
        }
    }
}