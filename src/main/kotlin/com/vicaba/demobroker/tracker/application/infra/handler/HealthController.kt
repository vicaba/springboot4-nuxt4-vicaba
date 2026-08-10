package com.vicaba.demobroker.tracker.application.infra.handler

import com.vicaba.demobroker.tracker.application.infra.logger.logger
import com.vicaba.demobroker.tracker.contract.api.HealthApi
import com.vicaba.demobroker.tracker.contract.model.HealthResponseVM
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController(
    private val healthEndpoint: HealthEndpoint,
) : HealthApi {
    override fun getHealth(): ResponseEntity<HealthResponseVM> {
        logger.info("Fetching health status from Spring Boot Actuator")
        val actuatorHealth = healthEndpoint.health()
        val statusCode = actuatorHealth.status.code
        val statusEnum =
            try {
                HealthResponseVM.Status.valueOf(statusCode)
            } catch (e: IllegalArgumentException) {
                HealthResponseVM.Status.UNKNOWN
            }
        return ResponseEntity.ok(HealthResponseVM(status = statusEnum))
    }
}
