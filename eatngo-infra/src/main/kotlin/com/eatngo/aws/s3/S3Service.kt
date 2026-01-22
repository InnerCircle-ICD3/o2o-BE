package com.eatngo.aws.s3

import com.eatngo.common.circuitbreaker.annotation.RedisCircuitBreaker
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
import java.time.Duration
import java.util.*

@Service
class S3Service(
    private val s3Client: S3Client,
    private val s3PreSigner: S3Presigner,
    @Value("\${cloud.aws.s3.bucket}") private val bucket: String,
) : FileStorageService {
    companion object {
        private val log = LoggerFactory.getLogger(S3Service::class.java)
        private val PRE_SIGNED_URL_DURATION = Duration.ofMinutes(10)
    }

    @RedisCircuitBreaker(
        name = "s3-presigned-url",
        fallbackMethod = "generatePreSignedUploadUrlFallback",
        failureThreshold = 5,
        timeoutMinutes = 1L,
    )
    override fun generatePreSignedUploadUrl(
        fileName: String,
        contentType: String,
        folderPath: String,
    ): Pair<String, String> {
        val image = Image(fileName, contentType, folderPath)
        val s3Key: String = createS3Key(image)

        val putObjectRequest =
            PutObjectRequest
                .builder()
                .bucket(bucket)
                .key(s3Key)
//                .contentType(contentType)
                .build()

        val putObjectPreSignRequest =
            PutObjectPresignRequest
                .builder()
                .signatureDuration(PRE_SIGNED_URL_DURATION)
                .putObjectRequest(putObjectRequest)
                .build()

        val preSignedRequest = s3PreSigner.presignPutObject(putObjectPreSignRequest)

        log.info("Generated pre-signed URL for key: {}, URL: {}", s3Key, preSignedRequest.url())

        return Pair(preSignedRequest.url().toString(), s3Key)
    }

    private fun generatePreSignedUploadUrlFallback(
        fileName: String,
        contentType: String,
        folderPath: String,
        ex: Exception
    ): Pair<String, String> {
        log.error("S3 pre-signed URL generation failed", ex)
        throw IllegalStateException("현재 이미지 업로드가 일시적으로 불가능합니다. 잠시 후 다시 시도해주세요.", ex)
    }

    private fun createS3Key(image: Image): String {
        var cleanedFolderPath = image.folderPath ?: ""

        if (cleanedFolderPath.isNotEmpty() && !cleanedFolderPath.endsWith("/")) {
            cleanedFolderPath += "/"
        }

        return cleanedFolderPath + UUID.randomUUID() + "_" + image.fileName
    }

    @RedisCircuitBreaker(
        name = "s3-delete-file",
        fallbackMethod = "deleteFileFallback",
        failureThreshold = 3,
        timeoutMinutes = 5L,
    )
    override fun deleteFile(key: String) { // ex. images/uuid_filename.jpg"
        val deleteObjectRequest =
            DeleteObjectRequest
                .builder()
                .bucket(bucket)
                .key(key)
                .build()

        s3Client.deleteObject(deleteObjectRequest)
    }

    private fun deleteFileFallback(key: String, ex: Exception) {
        log.error(
            "[S3_DELETE_FAILED] key: {}, operation: DELETE, error: {}, " +
            "action: DB는 이미 삭제됨. 수동 재시도 필요.",
            key, ex.message, ex
        )
        // TODO: 대안 1 - Slack/Teams 알림 전송
        // TODO: 대안 2 - Datadog/NewRelic 알림 설정
        // TODO: 대안 3 - 주간 리포트에 실패 건수 포함
    }

    @RedisCircuitBreaker(
        name = "s3-resolve-image-url",
        fallbackMethod = "resolveImageUrlFallback",
        failureThreshold = 5,
        timeoutMinutes = 2L,
    )
    override fun resolveImageUrl(key: String): String {
        if (key.startsWith("http://") || key.startsWith("https://")) {
            return key
        }

        val getUrlRequest =
            GetUrlRequest
                .builder()
                .bucket(bucket)
                .key(key) // s3 key
                .build()

        return s3Client.utilities().getUrl(getUrlRequest).toString()
    }

    private fun resolveImageUrlFallback(key: String, ex: Exception): String {
        log.warn("S3 resolveImageUrl failed for key: $key, returning empty string", ex)
        return ""  // ProductService에서 ?.let()으로 안전하게 처리됨
    }
}
