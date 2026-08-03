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
class HelloHandlerSpec(
    private val context: WebApplicationContext,
) : FunSpec() {
    private lateinit var mockMvc: MockMvc

    init {
        beforeTest {
            mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
        }

        test("GET /api/hello should return Hello message with OK status") {
            mockMvc
                .get("/api/hello") {
                    accept = MediaType.TEXT_PLAIN
                }.andExpect {
                    status { isOk() }
                    content { contentType(MediaType.TEXT_PLAIN) }
                    content { string("Hello World") }
                }
        }

        test("GET /api/hello should return TEXT_PLAIN content type") {
            mockMvc
                .get("/api/hello") {
                    accept = MediaType.TEXT_PLAIN
                }.andExpect {
                    content { contentType(MediaType.TEXT_PLAIN) }
                }
        }

        test("GET /api/hello should handle multiple requests") {
            repeat(2) {
                mockMvc
                    .get("/api/hello") {
                        accept = MediaType.TEXT_PLAIN
                    }.andExpect {
                        status { isOk() }
                    }
            }
        }
    }
}
