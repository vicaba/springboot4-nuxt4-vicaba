package com.example.demo.handler

import com.example.demo.logger.logger
import com.example.demo.model.HelloResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${application.api-base-path}")
class HelloController {
    @GetMapping("/hello")
    fun getHello(): ResponseEntity<HelloResponse> {
        logger.info("Called hello api")
        return ResponseEntity.ok(HelloResponse(message = "Hello World!!"))
    }
}
