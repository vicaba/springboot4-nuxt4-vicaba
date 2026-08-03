package com.example.demo.handler

import com.example.demo.logger.logger
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${application.api-base-path}")
class HelloController {
    @GetMapping("/hello")
    fun getHello(): ResponseEntity<String> {
        logger.info("Called hello api")
        return ResponseEntity
            .ok()
            .contentType(MediaType.TEXT_PLAIN)
            .body("Hello World!!")
    }
}
