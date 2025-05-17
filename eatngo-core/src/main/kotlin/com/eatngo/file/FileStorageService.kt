package com.eatngo.file

import org.springframework.web.multipart.MultipartFile

interface FileStorageService {
    fun saveFile(
        image: MultipartFile,
        path: String
    ): String

    fun deleteFile(key: String)

    fun resolveImageUrl(imageUrl: String): String
}