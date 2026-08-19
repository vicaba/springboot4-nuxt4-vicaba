package com.vicaba.demobroker.tracker.application.infra.controller

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
    fun `GET root with Accept text html should forward to index html with OK status`() {
        mockMvc
            .get("/") {
                accept = MediaType.TEXT_HTML
            }.andExpect {
                status { isOk() }
                forwardedUrl("index.html")
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
    fun `GET swagger-ui html should redirect to swagger-ui index html`() {
        mockMvc
            .get("/swagger-ui.html")
            .andExpect {
                status { is3xxRedirection() }
                header { string("Location", "/swagger-ui/index.html") }
            }
    }

    @Test
    fun `GET swagger-ui index html should return OK status with text html`() {
        mockMvc
            .get("/swagger-ui/index.html")
            .andExpect {
                status { isOk() }
                content { contentTypeCompatibleWith(MediaType.TEXT_HTML) }
            }
    }

    @Test
    fun `GET v3 api-docs should return OK status with application json`() {
        mockMvc
            .get("/v3/api-docs")
            .andExpect {
                status { isOk() }
                content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
            }
    }

    @Test
    fun `GET nonexistent api route should return 404 Not Found`() {
        mockMvc
            .get("/api/nonexistent")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `GET missing static file should return 404 Not Found`() {
        mockMvc
            .get("/nonexistent.js")
            .andExpect {
                status { isNotFound() }
            }
    }
}
