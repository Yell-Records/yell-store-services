package com.yellrecords.services.jobs

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jobs.cart-item-cleanup")
data class CartItemCleanupJobProperties(
    val enabled: Boolean,
    val cutoffDays: Long,
)
