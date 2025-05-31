//package com.eatngo.subscription.notification
//
//import com.eatngo.redis.utils.writeValueToJson
//import com.eatngo.subscription.event.SubscriptionEvent
//import com.eatngo.subscription.event.SubscriptionEventStatus
//import com.fasterxml.jackson.databind.ObjectMapper
//import org.slf4j.LoggerFactory
//import org.springframework.context.event.EventListener
//import org.springframework.data.redis.core.RedisTemplate
//import org.springframework.stereotype.Service
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
//import java.io.IOException
//import java.util.concurrent.ConcurrentHashMap
//import java.util.concurrent.Executors
//import java.util.concurrent.TimeUnit
//
///**
// * 구독 알림 서비스
// * SSE(Server-Sent Events)
// */
//@Service
//class SubscriptionNotificationService(
//    private val redisTemplate: RedisTemplate<String, String>,
//    private val objectMapper: ObjectMapper
//) {
//    private val logger = LoggerFactory.getLogger(this::class.java)
//
//    // SSE 연결 저장소 (사용자 ID -> 이미터)
//    private val customerEmitters = ConcurrentHashMap<Long, MutableList<SseEmitter>>()
//    private val storeOwnerEmitters = ConcurrentHashMap<Long, MutableList<SseEmitter>>()
//
//    // Redis 채널 (Pub/Sub)
//    private val SUBSCRIPTION_NOTIFICATION_CHANNEL = "subscription:notifications"
//
//    // SSE 이벤트 타입
//    enum class NotificationType {
//        SUBSCRIPTION_CREATED, SUBSCRIPTION_CANCELED, SUBSCRIPTION_RESUMED
//    }
//
//    // SSE 이벤트 데이터
//    data class NotificationPayload(
//        val type: NotificationType,
//        val subscriptionId: Long,
//        val customerId: Long,
//        val storeId: Long,
//        val timestamp: String,
//        val message: String
//    )
//
//    /**
//     * 고객 SSE 연결 생성
//     * @param customerId 고객 ID
//     * @return SseEmitter 연결
//     */
//    fun createCustomerEmitter(customerId: Long): SseEmitter {
//        val emitter = SseEmitter(TimeUnit.MINUTES.toMillis(30)) // 30분 타임아웃
//
//        // 기존 연결 목록 가져오기 또는 새로 생성
//        val emitters = customerEmitters.getOrPut(customerId) { mutableListOf() }
//        emitters.add(emitter)
//
//        // 연결 완료 이벤트 전송
//        try {
//            emitter.send(SseEmitter.event()
//                .name("connect")
//                .data("구독 알림 서비스에 연결되었습니다.")
//            )
//        } catch (e: IOException) {
//            logger.error("SSE 연결 초기화 실패: {}", e.message)
//            emitter.completeWithError(e)
//        }
//
//        // 연결 종료 시 이미터 제거
//        emitter.onCompletion {
//            emitters.remove(emitter)
//            if (emitters.isEmpty()) {
//                customerEmitters.remove(customerId)
//            }
//        }
//
//        // 연결 오류 시 이미터 제거
//        emitter.onError { ex ->
//            logger.error("SSE 연결 오류: {}", ex.message)
//            emitters.remove(emitter)
//            if (emitters.isEmpty()) {
//                customerEmitters.remove(customerId)
//            }
//        }
//
//        // 연결 타임아웃 시 이미터 제거
//        emitter.onTimeout {
//            emitter.complete()
//            emitters.remove(emitter)
//            if (emitters.isEmpty()) {
//                customerEmitters.remove(customerId)
//            }
//        }
//
//        return emitter
//    }
//
//    /**
//     * 점주 SSE 연결 생성
//     * @param storeOwnerId 점주 ID
//     * @return SseEmitter 연결
//     */
//    fun createStoreOwnerEmitter(storeOwnerId: Long): SseEmitter {
//        val emitter = SseEmitter(TimeUnit.MINUTES.toMillis(30)) // 30분 타임아웃
//
//        // 기존 연결 목록 가져오기 또는 새로 생성
//        val emitters = storeOwnerEmitters.getOrPut(storeOwnerId) { mutableListOf() }
//        emitters.add(emitter)
//
//        // 연결 완료 이벤트 전송
//        try {
//            emitter.send(SseEmitter.event()
//                .name("connect")
//                .data("구독 알림 서비스에 연결되었습니다.")
//            )
//        } catch (e: IOException) {
//            logger.error("SSE 연결 초기화 실패: {}", e.message)
//            emitter.completeWithError(e)
//        }
//
//        // 연결 종료 시 이미터 제거
//        emitter.onCompletion {
//            emitters.remove(emitter)
//            if (emitters.isEmpty()) {
//                storeOwnerEmitters.remove(storeOwnerId)
//            }
//        }
//
//        // 연결 오류 시 이미터 제거
//        emitter.onError { ex ->
//            logger.error("SSE 연결 오류: {}", ex.message)
//            emitters.remove(emitter)
//            if (emitters.isEmpty()) {
//                storeOwnerEmitters.remove(storeOwnerId)
//            }
//        }
//
//        // 연결 타임아웃 시 이미터 제거
//        emitter.onTimeout {
//            emitter.complete()
//            emitters.remove(emitter)
//            if (emitters.isEmpty()) {
//                storeOwnerEmitters.remove(storeOwnerId)
//            }
//        }
//
//        return emitter
//    }
//
//    /**
//     * 구독 이벤트 처리
//     */
//    @EventListener
//    fun handleSubscriptionEvent(event: SubscriptionEvent) {
//        val (type, message) = when (event.status) {
//            SubscriptionEventStatus.CREATED -> NotificationType.SUBSCRIPTION_CREATED to "새로운 매장 구독이 생성되었습니다."
//            SubscriptionEventStatus.CANCELED -> NotificationType.SUBSCRIPTION_CANCELED to "매장 구독이 취소되었습니다."
//            SubscriptionEventStatus.RESUMED -> NotificationType.SUBSCRIPTION_RESUMED to "매장 구독이 재개되었습니다."
//        }
//
//        // 알림 데이터 생성
//        val payload = NotificationPayload(
//            type = type,
//            subscriptionId = event.subscriptionId,
//            customerId = event.customerId,
//            storeId = event.storeId,
//            timestamp = event.timestamp.toString(),
//            message = message
//        )
//
//        // Redis에 알림 발행
//        publishNotification(payload)
//
//        // 비동기로 이미터에 알림 전송
//        sendNotificationAsync(event.customerId, null, payload)
//    }
//
//    /**
//     * Redis에 알림 발행
//     */
//    private fun publishNotification(payload: NotificationPayload) {
//        try {
//            val jsonPayload = objectMapper.writeValueToJson(payload)
//            redisTemplate.convertAndSend(SUBSCRIPTION_NOTIFICATION_CHANNEL, jsonPayload)
//        } catch (e: Exception) {
//            logger.error("알림 발행 실패: {}", e.message)
//        }
//    }
//
//    // 비동기 실행기
//    private val executor = Executors.newFixedThreadPool(10)
//
//    /**
//     * 비동기로 이미터에 알림 전송
//     */
//    private fun sendNotificationAsync(
//        customerId: Long,
//        storeOwnerId: Long? = null,
//        payload: NotificationPayload
//    ) {
//        executor.submit {
//            try {
//                // 고객에게 알림 전송
//                val customerEmitterList = customerEmitters[customerId]
//                if (!customerEmitterList.isNullOrEmpty()) {
//                    val deadEmitters = mutableListOf<SseEmitter>()
//
//                    // 각 이미터에 알림 전송
//                    customerEmitterList.forEach { emitter ->
//                        try {
//                            emitter.send(
//                                SseEmitter.event()
//                                    .name(payload.type.name.lowercase())
//                                    .data(objectMapper.writeValueToJson(payload))
//                            )
//                        } catch (e: Exception) {
//                            logger.error("SSE 알림 전송 실패: {}", e.message)
//                            emitter.completeWithError(e)
//                            deadEmitters.add(emitter)
//                        }
//                    }
//
//                    // 죽은 이미터 제거
//                    if (deadEmitters.isNotEmpty()) {
//                        customerEmitterList.removeAll(deadEmitters)
//                        if (customerEmitterList.isEmpty()) {
//                            customerEmitters.remove(customerId)
//                        }
//                    }
//                }
//
//                // 점주에게 알림 전송
//                if (storeOwnerId != null) {
//                    val storeOwnerEmitterList = storeOwnerEmitters[storeOwnerId]
//                    if (!storeOwnerEmitterList.isNullOrEmpty()) {
//                        val deadEmitters = mutableListOf<SseEmitter>()
//
//                        // 각 이미터에 알림 전송
//                        storeOwnerEmitterList.forEach { emitter ->
//                            try {
//                                emitter.send(
//                                    SseEmitter.event()
//                                        .name(payload.type.name.lowercase())
//                                        .data(objectMapper.writeValueToJson(payload))
//                                )
//                            } catch (e: Exception) {
//                                logger.error("SSE 알림 전송 실패: {}", e.message)
//                                emitter.completeWithError(e)
//                                deadEmitters.add(emitter)
//                            }
//                        }
//
//                        // 죽은 이미터 제거
//                        if (deadEmitters.isNotEmpty()) {
//                            storeOwnerEmitterList.removeAll(deadEmitters)
//                            if (storeOwnerEmitterList.isEmpty()) {
//                                storeOwnerEmitters.remove(storeOwnerId)
//                            }
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                logger.error("알림 처리 중 오류 발생: {}", e.message)
//            }
//        }
//    }
//}