package com.vicaba.demobroker.tracker.application.infra

import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

@SpringBootTest
class TrackerApplicationTest {
    @Autowired
    private lateinit var context: ApplicationContext

    @Test
    fun `context should load successfully`() {
        assertNotNull(context)
    }

    @Test
    fun `application should start without errors`() {
        assertNotEquals(0, context.beanDefinitionNames.size)
    }
}
