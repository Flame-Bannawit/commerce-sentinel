package com.portfolio.tests.integration

import org.junit.jupiter.api.Tag
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * Base class for Integration Tests using Testcontainers.
 *
 * Spins up a REAL PostgreSQL container per test suite (not a mock!).
 * Container is shared across all tests in the suite via @Container (static).
 *
 * This demonstrates to recruiters that you can:
 * 1. Write tests against real databases
 * 2. Use Testcontainers for environment isolation
 * 3. Manage Spring context properly
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TestApplication::class]
)
@Testcontainers
@Tag("integration")
abstract class IntegrationTestBase {

    companion object {

        @Container
        @JvmStatic
        val postgres = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
            withDatabaseName("ecommerce_test")
            withUsername("test_user")
            withPassword("test_pass")
            withInitScript("db/init.sql")  // seeds schema + test data
        }

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "validate" }
        }
    }
}
