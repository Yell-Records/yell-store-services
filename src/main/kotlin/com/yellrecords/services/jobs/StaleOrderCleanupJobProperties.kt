package com.yellrecords.services.jobs

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jobs.stale-order-cleanup")
data class StaleOrderCleanupJobProperties(
    val enabled: Boolean,
    val cutoffDays: Long,
)
