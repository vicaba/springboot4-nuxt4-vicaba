package com.example.demo.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(
    val applicationProperties: ApplicationProperties,
) : WebMvcConfigurer {
    override fun addCorsMappings(corsRegistry: CorsRegistry) {
        val corsProperties = applicationProperties.cors
        corsRegistry
            .addMapping(corsProperties.mappingPathPattern)
            .allowedOrigins(*corsProperties.allowedOrigins.toTypedArray())
            .allowedMethods(*corsProperties.allowedMethods.toTypedArray())
            .maxAge(corsProperties.maxAge)
    }
}
