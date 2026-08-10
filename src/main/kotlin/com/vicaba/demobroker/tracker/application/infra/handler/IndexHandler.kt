package com.vicaba.demobroker.tracker.application.infra.handler

import com.vicaba.demobroker.tracker.application.infra.config.ApplicationProperties
import org.springframework.core.io.Resource
import org.springframework.http.CacheControl
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class IndexHandler(
    private val applicationProperties: ApplicationProperties,
) {
    fun getIndex(): ResponseEntity<Resource> =
        ResponseEntity
            .ok()
            .contentType(MediaType.TEXT_HTML)
            .cacheControl(CacheControl.noCache())
            .body(applicationProperties.indexFile)
}
