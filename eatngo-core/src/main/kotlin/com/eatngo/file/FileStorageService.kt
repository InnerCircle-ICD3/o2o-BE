package com.eatngo.file

interface FileStorageService {
    fun generatePreSignedUploadUrl(
        fileName: String,
        contentType: String,
        folderPath: String,
    ): Pair<String, String>

    fun deleteFile(key: String)

    fun resolveImageUrl(key: String): String
}