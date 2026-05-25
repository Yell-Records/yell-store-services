package com.yellrecords.services.jobs

import com.yellrecords.services.cart.CartItemRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
@EnableConfigurationProperties(CartItemCleanupJobProperties::class)
class CartItemCleanupJob(
    private val properties: CartItemCleanupJobProperties,
    private val cartItemRepository: CartItemRepository,
) {
    private val log = LoggerFactory.getLogger(CartItemCleanupJob::class.java)

    companion object {
        private const val LOG_PREFIX = "[CART ITEM CLEANUP]"
    }

    init {
        require(properties.cutoffDays > 0) { "$LOG_PREFIX cutoffDays must be > 0" }
    }

    @Transactional
    @Scheduled(cron = $$"${jobs.cart-item-cleanup.cron}")
    fun cleanupCartItems() {
        if (!properties.enabled) return

        log.info("$LOG_PREFIX Job started (cutoff = ${properties.cutoffDays} days).")

        val now = OffsetDateTime.now()
        val cutoff = now.minusDays(properties.cutoffDays)

        val cartItemsToClean = cartItemRepository.findCartItemsToClean(cutoff)

        log.info("$LOG_PREFIX Found ${cartItemsToClean.size} cart items to purge.")

        if (cartItemsToClean.isNotEmpty()) {
            cartItemsToClean
                .groupBy { it.guestSessionId }
                .forEach { (guestSessionId, items) ->
                    log.info(
                        "$LOG_PREFIX Removing ${items.size} items from session: $guestSessionId",
                    )

                    cartItemRepository.deleteAll(items)
                }

            log.info("$LOG_PREFIX Job finished.")
        }
    }
}
