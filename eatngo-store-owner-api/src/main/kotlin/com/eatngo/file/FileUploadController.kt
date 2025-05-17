package com.eatngo.file

import com.eatngo.file.dto.UploadRequestDto
import com.eatngo.file.dto.UploadResponseDto
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/image")
class FileUploadController(
    private val fileUploadService: FileStorageService
) {

    @PostMapping("/presigned-url")
    fun upload(@RequestBody uploadRequestDto: UploadRequestDto): UploadResponseDto =
        UploadResponseDto.from(
            fileUploadService.generatePreSignedUploadUrl(
                uploadRequestDto.fileName,
                uploadRequestDto.contentType,
                uploadRequestDto.folderPath
            )
        )
}