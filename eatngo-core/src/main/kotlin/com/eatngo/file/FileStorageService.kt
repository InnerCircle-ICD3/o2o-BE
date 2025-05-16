package com.eatngo.file

import org.springframework.web.multipart.MultipartFile

// TODO 모듈 common 으로 이동 필요
interface FileStorageService {
    fun saveFile(imageUrl: MultipartFile): String
}