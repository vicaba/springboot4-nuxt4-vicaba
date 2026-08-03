package com.example.demo.handler

import com.example.demo.model.HelloResponse
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class HelloHandlerSpec : FunSpec() {
    private val helloController = HelloController()

    init {
        test("getHello should return OK status") {
            val response = helloController.getHello()

            response.statusCode.value() shouldBe 200
        }

        test("getHello should return HelloResponse body") {
            val response = helloController.getHello()

            response.body shouldBe HelloResponse(message = "Hello World!")
        }
    }
}
