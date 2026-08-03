package com.example.demo.handler

import com.example.demo.model.HelloResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HelloHandlerTest {
    private val helloController = HelloController()

    @Test
    fun `getHello should return OK status`() {
        val response = helloController.getHello()
        assertEquals(200, response.statusCode.value())
    }

    @Test
    fun `getHello should return HelloResponse body`() {
        val response = helloController.getHello()
        assertEquals(HelloResponse(message = "Hello World!"), response.body)
    }
}
