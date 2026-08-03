package com.example.demo.logger

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import org.slf4j.Logger

class LoggerExtensionTest {
    @Test
    fun `logger should return a Logger instance`() {
        val instance = SampleClass()

        assertInstanceOf(Logger::class.java, instance.logger)
    }

    @Test
    fun `logger should have correct name matching the class`() {
        val instance = SampleClass()

        assertEquals(SampleClass::class.java.name, instance.logger.name)
    }

    @Test
    fun `logger should return consistent logger for different instances`() {
        val instance1 = SampleClass()
        val instance2 = SampleClass()

        assertEquals(instance2.logger.name, instance1.logger.name)
    }
}

private class SampleClass
