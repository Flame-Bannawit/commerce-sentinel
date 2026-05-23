package com.portfolio.tests.utils

import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.builder.ResponseSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import io.restassured.specification.ResponseSpecification
import org.junit.jupiter.api.BeforeAll

/**
 * Base class for all API tests.
 * Configures RestAssured with:
 * - Base URL from Config
 * - JSON content type
 * - Allure logging filter (auto-attaches request/response to report)
 * - Common response spec (2xx expected)
 */
abstract class ApiTestBase {

    companion object {
        lateinit var requestSpec: RequestSpecification
        lateinit var responseSpec: ResponseSpecification

        @JvmStatic
        @BeforeAll
        fun setupRestAssured() {
            RestAssured.baseURI = Config.apiBaseUrl
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

            requestSpec = RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(AllureRestAssured())  // ← Attaches to Allure report
                .log(LogDetail.METHOD)
                .log(LogDetail.URI)
                .build()

            responseSpec = ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .build()
        }

        /** Build a request spec with Bearer token for authenticated endpoints */
        fun authenticatedSpec(token: String): RequestSpecification =
            RequestSpecBuilder()
                .addRequestSpecification(requestSpec)
                .addHeader("Authorization", "Bearer $token")
                .build()
    }
}
