package com.example.demo.handler

import com.example.demo.api.HelloApi
import com.example.demo.logger.logger
import com.example.demo.model.HelloResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController : HelloApi {
    override fun getHello(): ResponseEntity<HelloResponse> {
        logger.info("Called hello api")
        return ResponseEntity.ok(HelloResponse(message = "Hello World!"))
    }
}
