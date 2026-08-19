package com.vicaba.demobroker.tracker.application.infra.config

import org.springframework.core.io.Resource
import org.springframework.web.servlet.resource.PathResourceResolver

class SpaResourceResolver(
    private val apiBasePath: String,
    private val indexFile: Resource,
) : PathResourceResolver() {
    override fun getResource(
        resourcePath: String,
        location: Resource,
    ): Resource? {
        val resource = super.getResource(resourcePath, location)
        return if (resource != null && resource.isReadable) {
            resource
        } else if (isSpaFallback(resourcePath)) {
            indexFile
        } else {
            null
        }
    }

    fun isSpaFallback(resourcePath: String): Boolean {
        val normalized = if (resourcePath.startsWith("/")) resourcePath else "/$resourcePath"
        val isApi = normalized == apiBasePath || normalized.startsWith("$apiBasePath/")
        val isSwagger =
            normalized.startsWith("/swagger-ui") ||
                normalized.startsWith("/v3/api-docs")
        val hasExtension = resourcePath.substringAfterLast('/').contains('.')
        return !isApi && !isSwagger && !hasExtension
    }
}
