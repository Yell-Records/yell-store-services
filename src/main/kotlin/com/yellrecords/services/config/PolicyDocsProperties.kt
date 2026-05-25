package com.yellrecords.services.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "policies")
class PolicyDocsProperties {
    lateinit var path: String
}
