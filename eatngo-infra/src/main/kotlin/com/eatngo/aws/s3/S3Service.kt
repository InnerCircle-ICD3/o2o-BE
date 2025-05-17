package com.eatngo.aws.s3

import com.eatngo.file.FileStorageService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetUrlRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.IOException
import java.util.UUID

@Service
class S3Service(
    private val s3Client: S3Client,
    @Value("\${cloud.aws.s3.bucket}") private val bucket: String
) : FileStorageService {
    companion object {
        private val log = LoggerFactory.getLogger(S3Service::class.java)
    }

    /*
    * @param path S3 버킷 내에 파일을 저장할 폴더 경로 ex. => images/, "profiles/
    * @return 저장된 파일의 S3 키 ex. images/uuid_filename.jpg
    */
    override fun saveFile(image: MultipartFile, path: String): String {
        val originalImage = image.originalFilename
            ?: throw IllegalArgumentException("원본 파일 이름을 찾을 수 없습니다.")

        val s3Key = createS3Key(originalImage, path)

        try {
            putFileToBucket(image, s3Key)
            return s3Key
        } catch (e: IOException) {
            log.error("파일 업로드 중 오류 발생: {}", e.message, e)
            throw IllegalArgumentException("파일 업로드에 실패했습니다.", e)
        }
    }

    private fun createS3Key(originalImage: String, folderPath: String?): String {
        var cleanedFolderPath = folderPath ?: ""

        if (cleanedFolderPath.isNotEmpty() && !cleanedFolderPath.endsWith("/")) {
            cleanedFolderPath += "/"
        }

        return cleanedFolderPath + UUID.randomUUID() + "_" + originalImage
    }

    private fun putFileToBucket(multipartFile: MultipartFile, s3Key: String) {
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(s3Key)
            .contentType(multipartFile.contentType)
            .build()

        multipartFile.inputStream.use { inputStream ->
            s3Client.putObject(
                putObjectRequest,
                RequestBody.fromInputStream(inputStream, multipartFile.size)
            )
        }
    }

    override fun deleteFile(key: String) { // ex. images/uuid_filename.jpg"
        try {
            val deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build()

            s3Client.deleteObject(deleteObjectRequest)
        } catch (e: Exception) {
            log.error("파일 삭제 중 오류 발생: {}", e.message, e)
            throw IllegalArgumentException("파일 삭제에 실패했습니다.", e)
        }
    }

    override fun resolveImageUrl(imageUrl: String): String {
        val getUrlRequest = GetUrlRequest.builder()
            .bucket(bucket)
            .key(imageUrl) // s3 key
            .build()

        return s3Client.utilities().getUrl(getUrlRequest).toString()
    }

}
