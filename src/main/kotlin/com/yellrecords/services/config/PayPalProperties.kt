package com.yellrecords.services.config

import com.yellrecords.services.paypal.PayPalMode
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "paypal")
class PayPalProperties {
    lateinit var clientId: String
    lateinit var clientSecret: String
    lateinit var mode: PayPalMode
}
