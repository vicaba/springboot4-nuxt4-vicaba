package com.example.demo.config

import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SpaFallbackConfigTest {
    @Nested
    inner class MatchingRequests {
        @Test
        fun `HTML root request matches`() {
            val request = mockRequest("/", "text/html")
            assertTrue(SpaFallbackConfig.isSpaRequest("/api", request))
        }

        @Test
        fun `non-API non-file path matches`() {
            val request = mockRequest("/about", "text/html")
            assertTrue(SpaFallbackConfig.isSpaRequest("/api", request))
        }

        @Test
        fun `nested path matches`() {
            val request = mockRequest("/users/profile", "text/html")
            assertTrue(SpaFallbackConfig.isSpaRequest("/api", request))
        }

        @Test
        fun `path with dot in directory but not in filename matches`() {
            val request = mockRequest("/v1.0/docs", "text/html")
            assertTrue(SpaFallbackConfig.isSpaRequest("/api", request))
        }

        @Test
        fun `multiple accept types including text html matches`() {
            val request = mockRequest("/about", "text/html,application/xhtml+xml,*/*")
            assertTrue(SpaFallbackConfig.isSpaRequest("/api", request))
        }

        @Test
        fun `path starting with API prefix but different word matches`() {
            val request = mockRequest("/apiary", "text/html")
            assertTrue(SpaFallbackConfig.isSpaRequest("/api", request))
        }
    }

    @Nested
    inner class NonMatchingRequests {
        @Test
        fun `API sub-path does not match`() {
            val request = mockRequest("/api/hello", "text/html")
            assertFalse(SpaFallbackConfig.isSpaRequest("/api", request))
        }

        @Test
        fun `API base path does not match`() {
            val request = mockRequest("/api", "text/html")
            assertFalse(SpaFallbackConfig.isSpaRequest("/api", request))
        }

        @Test
        fun `file extension does not match`() {
            val request = mockRequest("/favicon.ico", "text/html")
            assertFalse(SpaFallbackConfig.isSpaRequest("/api", request))
        }

        @Test
        fun `Nuxt asset does not match`() {
            val request = mockRequest("/_nuxt/entry.js", "text/html")
            assertFalse(SpaFallbackConfig.isSpaRequest("/api", request))
        }

        @Test
        fun `JSON accept does not match`() {
            val request = mockRequest("/about", "application/json")
            assertFalse(SpaFallbackConfig.isSpaRequest("/api", request))
        }

        @Test
        fun `empty accept does not match`() {
            val request = mockRequest("/about", null)
            assertFalse(SpaFallbackConfig.isSpaRequest("/api", request))
        }

        @Test
        fun `wildcard accept does not match`() {
            val request = mockRequest("/about", "*/*")
            assertFalse(SpaFallbackConfig.isSpaRequest("/api", request))
        }
    }

    private fun mockRequest(
        path: String,
        accept: String?,
    ): HttpServletRequest {
        val request = mockk<HttpServletRequest>()
        every { request.requestURI } returns path
        every { request.getHeader("Accept") } returns accept
        return request
    }
}
