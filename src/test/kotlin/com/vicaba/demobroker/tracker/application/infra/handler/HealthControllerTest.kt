package com.vicaba.demobroker.tracker.application.infra.handler

import com.vicaba.demobroker.tracker.contract.model.HealthResponse
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.health.actuate.endpoint.HealthDescriptor
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint
import org.springframework.boot.health.contributor.Status
import org.springframework.http.HttpStatus

class HealthControllerTest {
    private val healthEndpoint = mockk<HealthEndpoint>()
    private val healthController = HealthController(healthEndpoint)

    @Test
    fun `getHealth should return UP status when Actuator returns UP`() {
        val descriptor =
            mockk<HealthDescriptor> {
                every { status } returns Status.UP
            }
        every { healthEndpoint.health() } returns descriptor

        val response = healthController.getHealth()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(HealthResponse.Status.UP, response.body?.status)
    }

    @Test
    fun `getHealth should return DOWN status when Actuator returns DOWN`() {
        val descriptor =
            mockk<HealthDescriptor> {
                every { status } returns Status.DOWN
            }
        every { healthEndpoint.health() } returns descriptor

        val response = healthController.getHealth()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(HealthResponse.Status.DOWN, response.body?.status)
    }

    @Test
    fun `getHealth should return UNKNOWN status when Actuator returns unknown custom status`() {
        val descriptor =
            mockk<HealthDescriptor> {
                every { status } returns Status("CUSTOM_STATUS")
            }
        every { healthEndpoint.health() } returns descriptor

        val response = healthController.getHealth()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(HealthResponse.Status.UNKNOWN, response.body?.status)
    }
}
