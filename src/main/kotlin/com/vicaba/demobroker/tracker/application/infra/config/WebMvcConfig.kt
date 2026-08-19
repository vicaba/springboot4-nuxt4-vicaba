package com.vicaba.demobroker.tracker.application.infra.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(
    private val applicationProperties: ApplicationProperties,
) : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        val cors = applicationProperties.cors
        registry
            .addMapping(cors.mappingPathPattern)
            .allowedOrigins(*cors.allowedOrigins.toTypedArray())
            .allowedMethods(*cors.allowedMethods.toTypedArray())
            .maxAge(cors.maxAge)
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry
            .addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
            .resourceChain(true)
            .addResolver(
                SpaResourceResolver(
                    apiBasePath = applicationProperties.apiBasePath,
                    indexFile = applicationProperties.indexFile,
                ),
            )
    }
}
