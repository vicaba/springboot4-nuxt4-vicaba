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
class HelloControllerIntegrationTest {
    @Autowired
    private lateinit var context: WebApplicationContext

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    fun `GET api hello should return Hello message with OK status`() {
        mockMvc
            .get("/api/hello") {
                accept = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.message") { value("Hello World!") }
            }
    }

    @Test
    fun `GET api hello should handle multiple requests`() {
        repeat(2) {
            mockMvc
                .get("/api/hello") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                }
        }
    }
}
