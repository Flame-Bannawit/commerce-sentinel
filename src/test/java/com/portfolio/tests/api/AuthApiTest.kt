package com.portfolio.tests.api

import com.portfolio.tests.models.LoginRequest
import com.portfolio.tests.models.LoginResponse
import com.portfolio.tests.utils.ApiTestBase
import com.portfolio.tests.utils.TestDataFactory
import io.qameta.allure.*
import io.restassured.RestAssured.given
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*

@Epic("E-Commerce API")
@Feature("Authentication")
@Tag("api")
@Tag("auth")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AuthApiTest : ApiTestBase() {

    companion object {
        private val validCredentials = TestDataFactory.loginRequest()
    }

    // ─── Happy Path ────────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @Story("User Login")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Valid credentials should return a JWT token")
    fun `login with valid credentials returns token`() {
        val response = Given {
            spec(requestSpec)
            body(validCredentials)
        } When {
            post("/auth/login")
        } Then {
            statusCode(200)
        } Extract {
            `as`(LoginResponse::class.java)
        }

        assertThat(response.token)
            .isNotBlank()
            .hasSizeGreaterThan(10)
    }

    @Test
    @Order(2)
    @Story("User Login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Login endpoint should respond within acceptable time")
    fun `login response time is within threshold`() {
        Given {
            spec(requestSpec)
            body(validCredentials)
        } When {
            post("/auth/login")
        } Then {
            statusCode(200)
            time(org.hamcrest.Matchers.lessThan(3000L))
        }
    }

    // ─── Negative Cases ────────────────────────────────────────────────────────

    @Test
    @Order(3)
    @Story("Login Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Wrong password should return 401 Unauthorized")
    fun `login with wrong password returns 401`() {
        val invalidRequest = LoginRequest(
            username = validCredentials.username,
            password = "wrong_password_123"
        )

        Given {
            spec(requestSpec)
            body(invalidRequest)
        } When {
            post("/auth/login")
        } Then {
            statusCode(401)
        }
    }

    @Test
    @Order(4)
    @Story("Login Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Non-existent user should return 401")
    fun `login with non-existent user returns 401`() {
        val ghostUser = LoginRequest(
            username = "user_that_does_not_exist_xyz",
            password = "SomePassword@123"
        )

        Given {
            spec(requestSpec)
            body(ghostUser)
        } When {
            post("/auth/login")
        } Then {
            statusCode(401)
        }
    }

    @Test
    @Order(5)
    @Story("Login Validation")
    @Severity(SeverityLevel.MINOR)
    @Description("Empty credentials should return 400 Bad Request")
    fun `login with empty body returns 400`() {
        Given {
            spec(requestSpec)
            body("{}")
        } When {
            post("/auth/login")
        } Then {
            statusCode(400)
        }
    }

    // ─── Parameterized ─────────────────────────────────────────────────────────

    @ParameterizedTest
    @Order(6)
    @Story("Login Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Various invalid credential combinations should all fail")
    @MethodSource("invalidCredentialProvider")
    fun `login with invalid credentials is rejected`(username: String, password: String) {
        Given {
            spec(requestSpec)
            body(LoginRequest(username, password))
        } When {
            post("/auth/login")
        } Then {
            statusCode(org.hamcrest.Matchers.oneOf(400, 401))
        }
    }

    companion object {
        @JvmStatic
        fun invalidCredentialProvider() = listOf(
            org.junit.jupiter.params.provider.Arguments.of("", "password"),
            org.junit.jupiter.params.provider.Arguments.of("username", ""),
            org.junit.jupiter.params.provider.Arguments.of("", ""),
            org.junit.jupiter.params.provider.Arguments.of("a", "b")
        )
    }
}
