package com.eatngo.order.schedule

import com.eatngo.order.usecase.OldOrderCancelUseCase
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class OrderScheduler(
    private val oldOrderCancelUseCase: OldOrderCancelUseCase,
) {
    private val logger = LoggerFactory.getLogger(OrderScheduler::class.java)

    @Scheduled(cron = "0 */5 * * * *") // 매 5분 마다 실행
    @SchedulerLock(
        name = "myUniqueRedisJob",
        lockAtMostFor = "5m", // 락을 최대 10분까지 유지
        lockAtLeastFor = "4m", // 최소 30초는 락 유지
    )
    fun execute() {
        logger.info("Starting oldOrder Cancel")
        kotlin
            .runCatching {
                var lastId: Long? = null
                run {
                    lastId = oldOrderCancelUseCase.execute(lastId)
                }
                while (lastId != null) {
                    lastId = oldOrderCancelUseCase.execute(lastId)
                }
            }.onFailure {
                logger.info("End oldOrder Failed")
            }.onSuccess {
                logger.info("End oldOrder Cancel")
            }
    }
}
