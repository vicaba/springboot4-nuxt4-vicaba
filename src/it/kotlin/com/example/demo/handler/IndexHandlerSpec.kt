package com.example.demo.handler

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ApplyExtension(SpringExtension::class)
class IndexHandlerSpec(
    private val context: WebApplicationContext,
) : FunSpec() {
    private lateinit var mockMvc: MockMvc

    init {
        beforeTest {
            mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
        }

        test("GET / with Accept text/html should return index file content with OK status") {
            mockMvc
                .get("/") {
                    accept = MediaType.TEXT_HTML
                }.andExpect {
                    status { isOk() }
                    content { contentTypeCompatibleWith(MediaType.TEXT_HTML) }
                }
        }

        test("GET /about with Accept text/html should return SPA fallback with TEXT_HTML content type") {
            mockMvc
                .get("/about") {
                    accept = MediaType.TEXT_HTML
                }.andExpect {
                    status { isOk() }
                    content { contentTypeCompatibleWith(MediaType.TEXT_HTML) }
                }
        }

        test("GET / with Accept text/html should return application properties index file") {
            mockMvc
                .get("/") {
                    accept = MediaType.TEXT_HTML
                }.andExpect {
                    status { isOk() }
                }
        }
    }
}
