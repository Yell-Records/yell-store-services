package com.yellrecords.services.config

import com.yellrecords.services.policies.PolicyProvider
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "policies")
class PolicyDocsProperties {
    lateinit var path: String
    lateinit var provider: PolicyProvider
    lateinit var bucket: String
    lateinit var baseUrl: String
}
