package com.portfolio.tests.integration

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Minimal Spring Boot application for integration tests.
 * This simulates a real app — in a real project you'd import your actual app class.
 */
@SpringBootApplication
class TestApplication

fun main(args: Array<String>) {
    runApplication<TestApplication>(*args)
}
