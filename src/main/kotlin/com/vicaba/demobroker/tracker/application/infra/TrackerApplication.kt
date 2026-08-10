package com.vicaba.demobroker.tracker.application.infra

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.vicaba.demobroker.tracker"])
class TrackerApplication

fun main(args: Array<String>) {
    runApplication<TrackerApplication>(*args)
}
