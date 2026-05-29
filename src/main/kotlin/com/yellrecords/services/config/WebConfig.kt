package com.yellrecords.services.config

import com.yellrecords.services.images.ImageProvider
import com.yellrecords.services.logging.RequestLoggingInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/** Overrides web access to avoid having to allow CORS on each web controller. */
@Configuration
class WebConfig(
    private val interceptor: RequestLoggingInterceptor,
    private val imageConfig: ImageUploadProperties,
    private val corsProps: CorsProps,
) : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry
            .addMapping("/**")
            .allowedOriginPatterns(*corsProps.allowedOrigins.toTypedArray())
            .allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        if (imageConfig.provider == ImageProvider.LOCAL) {
            registry
                .addResourceHandler("/uploads/**")
                .addResourceLocations("file:${imageConfig.uploadDir}/")
        }
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(interceptor)
    }
}
