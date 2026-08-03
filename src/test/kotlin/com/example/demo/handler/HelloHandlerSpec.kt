package com.example.demo.handler

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.http.MediaType

class HelloHandlerSpec : FunSpec() {
    private val helloController = HelloController()

    init {
        test("getHello should return OK status") {
            val response = helloController.getHello()

            response.statusCode.value() shouldBe 200
        }

        test("getHello should return TEXT_PLAIN content type") {
            val response = helloController.getHello()

            response.headers.contentType shouldBe MediaType.TEXT_PLAIN
        }

        test("getHello should return Hello World body") {
            val response = helloController.getHello()

            response.body shouldBe "Hello World"
        }
    }
}
