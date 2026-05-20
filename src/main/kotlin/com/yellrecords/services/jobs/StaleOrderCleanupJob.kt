package com.yellrecords.services.jobs

import com.yellrecords.services.order.OrderRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
@EnableConfigurationProperties(StaleOrderCleanupJobProperties::class)
class StaleOrderCleanupJob(
    private val properties: StaleOrderCleanupJobProperties,
    private val orderRepository: OrderRepository,
) {
    private val log = LoggerFactory.getLogger(StaleOrderCleanupJob::class.java)

    companion object {
        private const val LOG_PREFIX = "[STALE ORDER CLEANUP]"
    }

    init {
        require(properties.cutoffDays > 0) { "$LOG_PREFIX cutoffDays must be > 0" }
    }

    /** After a set amount of days, delete any order entity with a status of "AWAITING_PAYMENT". */
    @Transactional
    @Scheduled(cron = $$"${jobs.stale-order-cleanup.cron}")
    fun autoCleanupStaleOrders() {
        if (!properties.enabled) return

        log.info("$LOG_PREFIX Job started (cutoff = ${properties.cutoffDays} days).")

        val now = OffsetDateTime.now()
        val cutoff = now.minusDays(properties.cutoffDays)

        val ordersToClean = orderRepository.findAllToClean(cutoff)

        log.info("$LOG_PREFIX Found ${ordersToClean.size} orders to purge.")

        if (ordersToClean.isNotEmpty()) {
            ordersToClean.forEach { order ->
                log.info("$LOG_PREFIX Removing Order with ID: ${order.id}")

                orderRepository.delete(order)
            }

            log.info("$LOG_PREFIX Job finished.")
        }
    }
}
