package com.eatngo.aws.s3

import com.eatngo.file.FileStorageService
import com.eatngo.product.domain.Image
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetUrlRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.io.IOException
import java.time.Duration
import java.util.*

@Service
class S3Service(
    private val s3Client: S3Client,
    private val s3PreSigner: S3Presigner,
    @Value("\${cloud.aws.s3.bucket}") private val bucket: String
) : FileStorageService {
    companion object {
        private val log = LoggerFactory.getLogger(S3Service::class.java)
        private val PRE_SIGNED_URL_DURATION = Duration.ofMinutes(10)
    }

    override fun generatePreSignedUploadUrl(
        fileName: String,
        contentType: String,
        folderPath: String
    ): Pair<String, String> {
        val image = Image(fileName, contentType, folderPath)
        val s3Key: String = createS3Key(image)

        try {
            val putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .contentType(contentType)
                .build()

            val putObjectPreSignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(PRE_SIGNED_URL_DURATION)
                .putObjectRequest(putObjectRequest)
                .build()

            val preSignedRequest = s3PreSigner.presignPutObject(putObjectPreSignRequest)

            log.info("Generated pre-signed URL for key: {}, URL: {}", s3Key, preSignedRequest.url())

            return Pair(preSignedRequest.url().toString(), s3Key)

        } catch (e: Exception) {
            log.error("Pre-signed URL 생성 중 오류 발생: {}", e.message, e)
            throw RuntimeException("Pre-signed URL 생성에 실패했습니다.", e)
        }
    }

    private fun createS3Key(image: Image): String {
        var cleanedFolderPath = image.folderPath ?: ""

        if (cleanedFolderPath.isNotEmpty() && !cleanedFolderPath.endsWith("/")) {
            cleanedFolderPath += "/"
        }

        return cleanedFolderPath + UUID.randomUUID() + "_" + image.fileName
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

    override fun resolveImageUrl(key: String): String {
        if (key.startsWith("http://") || key.startsWith("https://")) {
            return key
        }

        try {
            val getUrlRequest = GetUrlRequest.builder()
                .bucket(bucket)
                .key(key) // s3 key
                .build()

            return s3Client.utilities().getUrl(getUrlRequest).toString()
        } catch (e: Exception) {
            log.error("이미지 URL 변환 중 오류 발생: {}", e.message, e)
            throw IOException("이미지 URL 변환에 실패했습니다.", e)
        }

    }
}
