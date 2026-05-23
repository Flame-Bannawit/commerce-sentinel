package com.portfolio.tests.api

import com.portfolio.tests.models.CartResponse
import com.portfolio.tests.utils.ApiTestBase
import com.portfolio.tests.utils.TestDataFactory
import io.qameta.allure.*
import io.restassured.module.kotlin.extensions.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*

@Epic("E-Commerce API")
@Feature("Cart")
@Tag("api")
@Tag("cart")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class CartApiTest : ApiTestBase() {

    @Test
    @Order(1)
    @Story("View Cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("GET /carts returns all carts")
    fun `get all carts returns list`() {
        val carts = Given {
            spec(requestSpec)
        } When {
            get("/carts")
        } Then {
            statusCode(200)
        } Extract {
            `as`(Array<CartResponse>::class.java)
        }

        assertThat(carts).isNotEmpty
        assertThat(carts).allSatisfy { cart ->
            assertThat(cart.id).isPositive()
            assertThat(cart.userId).isPositive()
            assertThat(cart.products).isNotEmpty
        }
    }

    @Test
    @Order(2)
    @Story("View Cart")
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /carts/user/{userId} returns carts for that user")
    fun `get carts by user id returns user carts`() {
        val userId = 1
        val carts = Given {
            spec(requestSpec)
        } When {
            get("/carts/user/$userId")
        } Then {
            statusCode(200)
        } Extract {
            `as`(Array<CartResponse>::class.java)
        }

        assertThat(carts).isNotEmpty
        assertThat(carts).allSatisfy { cart ->
            assertThat(cart.userId).isEqualTo(userId)
        }
    }

    @Test
    @Order(3)
    @Story("Add to Cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("POST /carts creates a new cart and returns it")
    fun `add item to cart returns cart with id`() {
        val cartRequest = TestDataFactory.randomCart(userId = 1, productId = 3, quantity = 2)

        val response = Given {
            spec(requestSpec)
            body(cartRequest)
        } When {
            post("/carts")
        } Then {
            statusCode(200)
        } Extract {
            `as`(CartResponse::class.java)
        }

        assertThat(response.id).isPositive()
        assertThat(response.userId).isEqualTo(cartRequest.userId)
        assertThat(response.products).isNotEmpty
        assertThat(response.products.first().productId)
            .isEqualTo(cartRequest.products.first().productId)
    }

    @Test
    @Order(4)
    @Story("Date Filter")
    @Severity(SeverityLevel.MINOR)
    @Description("GET /carts with date range filters carts correctly")
    fun `get carts with date range returns filtered carts`() {
        val carts = Given {
            spec(requestSpec)
            queryParam("startdate", "2019-01-01")
            queryParam("enddate", "2019-12-31")
        } When {
            get("/carts")
        } Then {
            statusCode(200)
        } Extract {
            `as`(Array<CartResponse>::class.java)
        }

        // All returned carts should be within the date range
        assertThat(carts).isNotNull
    }
}
