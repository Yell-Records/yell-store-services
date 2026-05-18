package com.yellrecords.services.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cors")
data class CorsProps(
    val allowedOrigins: List<String> = emptyList(),
)
