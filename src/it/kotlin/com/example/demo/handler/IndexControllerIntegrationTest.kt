package com.example.demo.handler

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class IndexControllerIntegrationTest {
    @Autowired
    private lateinit var context: WebApplicationContext

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    fun `GET root with Accept text html should return index file content with OK status`() {
        mockMvc
            .get("/") {
                accept = MediaType.TEXT_HTML
            }.andExpect {
                status { isOk() }
                content { contentTypeCompatibleWith(MediaType.TEXT_HTML) }
            }
    }

    @Test
    fun `GET about with Accept text html should return SPA fallback with TEXT_HTML content type`() {
        mockMvc
            .get("/about") {
                accept = MediaType.TEXT_HTML
            }.andExpect {
                status { isOk() }
                content { contentTypeCompatibleWith(MediaType.TEXT_HTML) }
            }
    }

    @Test
    fun `GET root with Accept text html should return application properties index file`() {
        mockMvc
            .get("/") {
                accept = MediaType.TEXT_HTML
            }.andExpect {
                status { isOk() }
            }
    }
}
