package com.vicaba.demobroker.tracker.application.infra.handler

import com.example.demo.api.HealthApi
import com.example.demo.model.HealthResponse
import com.vicaba.demobroker.tracker.application.infra.logger.logger
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController(
    private val healthEndpoint: HealthEndpoint,
) : HealthApi {
    override fun getHealth(): ResponseEntity<HealthResponse> {
        logger.info("Fetching health status from Spring Boot Actuator")
        val actuatorHealth = healthEndpoint.health()
        val statusCode = actuatorHealth.status.code
        val statusEnum =
            try {
                HealthResponse.Status.valueOf(statusCode)
            } catch (e: IllegalArgumentException) {
                HealthResponse.Status.UNKNOWN
            }
        return ResponseEntity.ok(HealthResponse(status = statusEnum))
    }
}
