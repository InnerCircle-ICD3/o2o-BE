package com.eatngo.file

import com.eatngo.file.dto.UploadBatchRequestDto
import com.eatngo.file.dto.UploadRequestDto
import com.eatngo.file.dto.UploadResponseDto
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.CrossOrigin
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
    fun upload(@Valid @RequestBody uploadRequestDto: UploadRequestDto): UploadResponseDto =
        UploadResponseDto.from(
            fileUploadService.generatePreSignedUploadUrl(
                uploadRequestDto.fileName,
                uploadRequestDto.contentType,
                uploadRequestDto.folderPath
            )
        )

    @PostMapping("/presigned-urls")
    @CrossOrigin(origins = ["http://localhost:8081"])
    fun getPresignedUrls(@RequestBody uploadBatchRequestDto: UploadBatchRequestDto): List<UploadResponseDto> {
        return uploadBatchRequestDto.files.map { file ->
            UploadResponseDto.from(
                fileUploadService.generatePreSignedUploadUrl(
                    file.fileName,
                    file.contentType,
                    file.folderPath
                )
            )
        }
    }
}

