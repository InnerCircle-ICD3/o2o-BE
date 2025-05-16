package com.eatngo.file

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

// TODO 모듈 common 으로 이동 필요
@Service
class S3ServiceImpl : FileStorageService {
    override fun saveFile(imageUrl: MultipartFile): String {
        TODO("Not yet implemented")
    }
}