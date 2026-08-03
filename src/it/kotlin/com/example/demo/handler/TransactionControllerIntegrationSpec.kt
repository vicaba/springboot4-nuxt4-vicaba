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
class TransactionControllerIntegrationSpec(
    private val context: WebApplicationContext,
) : FunSpec() {
    private lateinit var mockMvc: MockMvc

    init {
        beforeTest {
            mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
        }

        test("GET /api/transactions should return transaction list with OK status") {
            mockMvc
                .get("/api/transactions") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$[0].symbol") { value("AAPL") }
                    jsonPath("$[0].op") { value("BUY") }
                    jsonPath("$[0].quantity") { value(50) }
                }
        }
    }
}
