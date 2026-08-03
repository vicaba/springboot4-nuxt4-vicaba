package com.example.demo.config

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest

class SpaFallbackConfigSpec : FunSpec() {
    init {
        context("matching requests") {
            test("HTML root request matches") {
                val request = mockRequest("/", "text/html")

                SpaFallbackConfig.isSpaRequest("/api", request) shouldBe true
            }

            test("non-API non-file path matches") {
                val request = mockRequest("/about", "text/html")

                SpaFallbackConfig.isSpaRequest("/api", request) shouldBe true
            }

            test("nested path matches") {
                val request = mockRequest("/users/profile", "text/html")

                SpaFallbackConfig.isSpaRequest("/api", request) shouldBe true
            }

            test("path with dot in directory but not in filename matches") {
                val request = mockRequest("/v1.0/docs", "text/html")

                SpaFallbackConfig.isSpaRequest("/api", request) shouldBe true
            }

            test("multiple accept types including text/html matches") {
                val request = mockRequest("/about", "text/html,application/xhtml+xml,*/*")

                SpaFallbackConfig.isSpaRequest("/api", request) shouldBe true
            }

            test("path starting with API prefix but different word matches") {
                val request = mockRequest("/apiary", "text/html")

                SpaFallbackConfig.isSpaRequest("/api", request) shouldBe true
            }
        }

        context("non-matching requests") {
            test("API sub-path does not match") {
                val request = mockRequest("/api/hello", "text/html")

                SpaFallbackConfig.isSpaRequest("/api", request) shouldBe false
            }

            test("API base path does not match") {
                val request = mockRequest("/api", "text/html")

                SpaFallbackConfig.isSpaRequest("/api", request) shouldBe false
            }

            test("file extension does not match") {
                val request = mockRequest("/favicon.ico", "text/html")

                SpaFallbackConfig.isSpaRequest("/api", request) shouldBe false
            }

            test("Nuxt asset does not match") {
                val request = mockRequest("/_nuxt/entry.js", "text/html")

                SpaFallbackConfig.isSpaRequest("/api", request) shouldBe false
            }

            test("JSON accept does not match") {
                val request = mockRequest("/about", "application/json")

                SpaFallbackConfig.isSpaRequest("/api", request) shouldBe false
            }

            test("empty accept does not match") {
                val request = mockRequest("/about", null)

                SpaFallbackConfig.isSpaRequest("/api", request) shouldBe false
            }

            test("wildcard accept does not match") {
                val request = mockRequest("/about", "*/*")

                SpaFallbackConfig.isSpaRequest("/api", request) shouldBe false
            }
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
