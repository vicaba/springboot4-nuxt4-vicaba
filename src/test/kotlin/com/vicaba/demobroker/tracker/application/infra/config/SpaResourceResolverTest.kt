package com.vicaba.demobroker.tracker.application.infra.config

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.core.io.Resource

class SpaResourceResolverTest {
    private val indexFile = mockk<Resource>()
    private val resolver = SpaResourceResolver(apiBasePath = "/api", indexFile = indexFile)

    @Nested
    inner class SpaFallbackMatching {
        @Test
        fun `root path matches`() {
            assertTrue(resolver.isSpaFallback("/"))
            assertTrue(resolver.isSpaFallback(""))
        }

        @Test
        fun `non-API non-file path matches`() {
            assertTrue(resolver.isSpaFallback("/about"))
            assertTrue(resolver.isSpaFallback("about"))
        }

        @Test
        fun `nested path matches`() {
            assertTrue(resolver.isSpaFallback("/users/profile"))
        }

        @Test
        fun `path with dot in directory but not in filename matches`() {
            assertTrue(resolver.isSpaFallback("/v1.0/docs"))
        }

        @Test
        fun `path starting with API prefix but different word matches`() {
            assertTrue(resolver.isSpaFallback("/apiary"))
        }
    }

    @Nested
    inner class SpaFallbackNonMatching {
        @Test
        fun `API sub-path does not match`() {
            assertFalse(resolver.isSpaFallback("/api/actuator/health"))
            assertFalse(resolver.isSpaFallback("api/actuator/health"))
        }

        @Test
        fun `API base path does not match`() {
            assertFalse(resolver.isSpaFallback("/api"))
            assertFalse(resolver.isSpaFallback("api"))
        }

        @Test
        fun `file extension does not match`() {
            assertFalse(resolver.isSpaFallback("/favicon.ico"))
            assertFalse(resolver.isSpaFallback("favicon.ico"))
        }

        @Test
        fun `Nuxt asset does not match`() {
            assertFalse(resolver.isSpaFallback("/_nuxt/entry.js"))
        }

        @Test
        fun `Swagger UI paths do not match`() {
            assertFalse(resolver.isSpaFallback("/swagger-ui.html"))
            assertFalse(resolver.isSpaFallback("/swagger-ui/index.html"))
            assertFalse(resolver.isSpaFallback("/swagger-ui/swagger-ui.css"))
            assertFalse(resolver.isSpaFallback("swagger-ui/index.html"))
        }

        @Test
        fun `OpenAPI docs paths do not match`() {
            assertFalse(resolver.isSpaFallback("/v3/api-docs"))
            assertFalse(resolver.isSpaFallback("/v3/api-docs/swagger-config"))
            assertFalse(resolver.isSpaFallback("v3/api-docs"))
        }
    }
}
