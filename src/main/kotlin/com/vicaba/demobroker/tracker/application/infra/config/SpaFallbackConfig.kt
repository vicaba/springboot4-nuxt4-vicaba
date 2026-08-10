package com.vicaba.demobroker.tracker.application.infra.config

import com.vicaba.demobroker.tracker.application.infra.handler.IndexHandler
import com.vicaba.demobroker.tracker.application.infra.logger.logger
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.io.ResourceLoader
import org.springframework.http.CacheControl
import org.springframework.http.MediaType
import org.springframework.http.MediaTypeFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import java.util.concurrent.TimeUnit

@Controller
class SpaFallbackConfig(
    private val applicationProperties: ApplicationProperties,
    private val indexHandler: IndexHandler,
    private val resourceLoader: ResourceLoader,
) {
    @GetMapping("/**")
    fun spa(request: HttpServletRequest): ResponseEntity<*> {
        val path = request.requestURI.removePrefix(request.contextPath)

        // SPA routes: no file extension and HTML-accepting → serve index.html
        if (isSpaRequest(applicationProperties.apiBasePath, request)) {
            logger.debug("SPA fallback for path: $path")
            return indexHandler.getIndex()
        }

        // Try to serve an existing static resource (has file extension)
        val staticResource = resourceLoader.getResource("classpath:/static$path")
        if (staticResource.exists()) {
            val cacheControl =
                if (path.startsWith("/_nuxt/")) {
                    CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic()
                } else {
                    CacheControl.noCache()
                }
            val mediaType = MediaTypeFactory.getMediaType(staticResource).orElse(MediaType.APPLICATION_OCTET_STREAM)
            return ResponseEntity
                .ok()
                .contentType(mediaType)
                .cacheControl(cacheControl)
                .body(staticResource)
        }

        return ResponseEntity.notFound().build<Void>()
    }

    companion object {
        fun isSpaRequest(
            apiBasePath: String,
            request: HttpServletRequest,
        ): Boolean {
            val path = request.requestURI
            val acceptsHtml =
                request.getHeader("Accept")?.split(",")?.any { it.trim().startsWith("text/html") } ?: false
            val isNotApi = !path.startsWith("$apiBasePath/") && path != apiBasePath
            val isNotFile = !path.substringAfterLast('/').contains('.')
            return acceptsHtml && isNotApi && isNotFile
        }
    }
}
