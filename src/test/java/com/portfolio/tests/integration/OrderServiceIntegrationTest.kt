package com.portfolio.tests.integration

import io.qameta.allure.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

/**
 * Integration Test: Order Service
 *
 * Tests real business logic against a live PostgreSQL container:
 * - Placing orders deducts stock
 * - Out-of-stock items are rejected
 * - Order totals are calculated correctly
 * - Failed payments trigger rollback
 *
 * These tests run against the ACTUAL database, not mocks!
 */
@Epic("Integration")
@Feature("Order Service")
@Tag("integration")
@Tag("orders")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class OrderServiceIntegrationTest : IntegrationTestBase() {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    // ─── Place Order ───────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @Story("Place Order")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Placing an order should succeed and return order details")
    fun `place order with in-stock item succeeds`() {
        val orderRequest = mapOf(
            "userId" to 1,
            "items" to listOf(
                mapOf("productId" to 1, "quantity" to 1)
            )
        )

        val response = restTemplate.postForEntity(
            "/api/orders",
            orderRequest,
            Map::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(response.body).isNotNull
        assertThat(response.body!!["id"]).isNotNull
        assertThat(response.body!!["status"]).isEqualTo("PENDING")
    }

    @Test
    @Order(2)
    @Story("Stock Management")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Placing an order should reduce product stock")
    fun `placing order reduces stock by ordered quantity`() {
        val orderQuantity = 2

        // Get stock before
        val productBefore = restTemplate.getForEntity("/api/products/1", Map::class.java)
        val stockBefore = productBefore.body!!["stock"] as Int

        // Place order
        val orderRequest = mapOf(
            "userId" to 1,
            "items" to listOf(mapOf("productId" to 1, "quantity" to orderQuantity))
        )
        restTemplate.postForEntity("/api/orders", orderRequest, Map::class.java)

        // Get stock after
        val productAfter = restTemplate.getForEntity("/api/products/1", Map::class.java)
        val stockAfter = productAfter.body!!["stock"] as Int

        assertThat(stockAfter).isEqualTo(stockBefore - orderQuantity)
    }

    @Test
    @Order(3)
    @Story("Stock Management")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Order for out-of-stock item should be rejected with 409 Conflict")
    fun `placing order for out-of-stock item returns 409`() {
        val orderRequest = mapOf(
            "userId" to 1,
            "items" to listOf(
                mapOf("productId" to 4, "quantity" to 1)  // product 4 has stock=0
            )
        )

        val response = restTemplate.postForEntity(
            "/api/orders",
            orderRequest,
            Map::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
        assertThat(response.body!!["error"].toString())
            .containsIgnoringCase("out of stock")
    }

    @Test
    @Order(4)
    @Story("Order Total")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Order total should equal sum of (unit_price × quantity)")
    fun `order total is calculated correctly`() {
        val orderRequest = mapOf(
            "userId" to 1,
            "items" to listOf(
                mapOf("productId" to 2, "quantity" to 2)  // 89.99 × 2 = 179.98
            )
        )

        val response = restTemplate.postForEntity(
            "/api/orders",
            orderRequest,
            Map::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)

        val total = (response.body!!["total"] as Number).toDouble()
        assertThat(total).isEqualTo(179.98)
    }

    // ─── Rollback on Failure ───────────────────────────────────────────────────

    @Test
    @Order(5)
    @Story("Transaction Rollback")
    @Severity(SeverityLevel.CRITICAL)
    @Description("If payment fails, stock should NOT be deducted (rollback)")
    fun `failed payment rolls back stock deduction`() {
        val productBefore = restTemplate.getForEntity("/api/products/1", Map::class.java)
        val stockBefore = productBefore.body!!["stock"] as Int

        // Force payment failure by using a special "fail" payment method
        val orderRequest = mapOf(
            "userId" to 1,
            "paymentMethod" to "FORCE_FAIL",
            "items" to listOf(mapOf("productId" to 1, "quantity" to 1))
        )

        restTemplate.postForEntity("/api/orders", orderRequest, Map::class.java)

        // Stock should be UNCHANGED after failed payment
        val productAfter = restTemplate.getForEntity("/api/products/1", Map::class.java)
        val stockAfter = productAfter.body!!["stock"] as Int

        assertThat(stockAfter)
            .withFailMessage("Stock was deducted despite payment failure — rollback didn't work!")
            .isEqualTo(stockBefore)
    }

    // ─── Get Orders ────────────────────────────────────────────────────────────

    @Test
    @Order(6)
    @Story("View Orders")
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /api/orders/{userId} returns orders for that user only")
    fun `get orders by user returns correct user orders`() {
        val userId = 1
        val response = restTemplate.getForEntity(
            "/api/orders/user/$userId",
            Array<Map<*, *>>::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotEmpty
        assertThat(response.body!!.toList()).allSatisfy { order ->
            assertThat(order["userId"]).isEqualTo(userId)
        }
    }
}
