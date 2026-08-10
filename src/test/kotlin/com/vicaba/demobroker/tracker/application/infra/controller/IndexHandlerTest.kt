package com.vicaba.demobroker.tracker.application.infra.controller

import com.vicaba.demobroker.tracker.application.infra.config.ApplicationProperties
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType

class IndexHandlerTest {
    private val applicationProperties =
        mockk<ApplicationProperties> {
            every { indexFile } returns ByteArrayResource("<html></html>".toByteArray())
        }
    private val indexHandler = IndexHandler(applicationProperties)

    @Test
    fun `getIndex should return OK status`() {
        val response = indexHandler.getIndex()
        assertEquals(200, response.statusCode.value())
    }

    @Test
    fun `getIndex should return TEXT_HTML content type`() {
        val response = indexHandler.getIndex()
        assertEquals(MediaType.TEXT_HTML, response.headers.contentType)
    }

    @Test
    fun `getIndex should set Cache-Control to no-cache`() {
        val response = indexHandler.getIndex()
        assertTrue(response.headers.cacheControl?.contains("no-cache") == true)
    }
}
