package com.yellrecords.services.config

import com.yellrecords.services.images.ImageProvider
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "images")
class ImageUploadProperties {
    lateinit var provider: ImageProvider

    var uploadDir: String? = null
    var bucket: String? = null
    var baseUrl: String? = null
}
