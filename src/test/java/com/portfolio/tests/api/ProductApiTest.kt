package com.portfolio.tests.api

import com.portfolio.tests.models.ProductResponse
import com.portfolio.tests.utils.ApiTestBase
import com.portfolio.tests.utils.TestDataFactory
import io.qameta.allure.*
import io.restassured.module.kotlin.extensions.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*

@Epic("E-Commerce API")
@Feature("Products")
@Tag("api")
@Tag("products")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ProductApiTest : ApiTestBase() {

    companion object {
        private var createdProductId: Int = -1
    }

    // ─── GET All Products ──────────────────────────────────────────────────────

    @Test
    @Order(1)
    @Story("List Products")
    @Severity(SeverityLevel.BLOCKER)
    @Description("GET /products should return a non-empty list")
    fun `get all products returns list`() {
        val products = Given {
            spec(requestSpec)
        } When {
            get("/products")
        } Then {
            statusCode(200)
        } Extract {
            `as`(Array<ProductResponse>::class.java)
        }

        assertThat(products)
            .isNotEmpty
            .allSatisfy { product ->
                assertThat(product.id).isPositive()
                assertThat(product.title).isNotBlank()
                assertThat(product.price).isPositive()
                assertThat(product.category).isNotBlank()
            }
    }

    @Test
    @Order(2)
    @Story("List Products")
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /products?limit=5 should return exactly 5 products")
    fun `get products with limit returns correct count`() {
        val products = Given {
            spec(requestSpec)
            queryParam("limit", 5)
        } When {
            get("/products")
        } Then {
            statusCode(200)
        } Extract {
            `as`(Array<ProductResponse>::class.java)
        }

        assertThat(products).hasSize(5)
    }

    @Test
    @Order(3)
    @Story("List Products")
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /products?sort=desc should return products in descending id order")
    fun `get products sorted descending`() {
        val products = Given {
            spec(requestSpec)
            queryParam("sort", "desc")
        } When {
            get("/products")
        } Then {
            statusCode(200)
        } Extract {
            `as`(Array<ProductResponse>::class.java)
        }

        val ids = products.map { it.id }
        assertThat(ids).isSortedAccordingTo(compareByDescending { it })
    }

    // ─── GET Single Product ────────────────────────────────────────────────────

    @Test
    @Order(4)
    @Story("Get Product")
    @Severity(SeverityLevel.CRITICAL)
    @Description("GET /products/{id} returns the correct product")
    fun `get product by id returns correct product`() {
        val product = Given {
            spec(requestSpec)
        } When {
            get("/products/1")
        } Then {
            statusCode(200)
        } Extract {
            `as`(ProductResponse::class.java)
        }

        assertThat(product.id).isEqualTo(1)
        assertThat(product.title).isNotBlank()
        assertThat(product.price).isGreaterThan(0.0)
        assertThat(product.rating).isNotNull
        assertThat(product.rating!!.rate).isBetween(0.0, 5.0)
    }

    @Test
    @Order(5)
    @Story("Get Product")
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /products/{id} with non-existent id returns 404")
    fun `get product with invalid id returns 404`() {
        Given {
            spec(requestSpec)
        } When {
            get("/products/999999")
        } Then {
            statusCode(404)
        }
    }

    // ─── GET Categories ────────────────────────────────────────────────────────

    @Test
    @Order(6)
    @Story("Product Categories")
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /products/categories returns all valid categories")
    fun `get product categories returns expected categories`() {
        val categories = Given {
            spec(requestSpec)
        } When {
            get("/products/categories")
        } Then {
            statusCode(200)
        } Extract {
            `as`(Array<String>::class.java)
        }

        val expectedCategories = setOf(
            "electronics", "jewelery", "men's clothing", "women's clothing"
        )

        assertThat(categories.toSet())
            .isNotEmpty
            .containsAnyElementsOf(expectedCategories)
    }

    @Test
    @Order(7)
    @Story("Product Categories")
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /products/category/{category} returns only products in that category")
    fun `get products by category returns filtered results`() {
        val category = "electronics"
        val products = Given {
            spec(requestSpec)
        } When {
            get("/products/category/$category")
        } Then {
            statusCode(200)
        } Extract {
            `as`(Array<ProductResponse>::class.java)
        }

        assertThat(products)
            .isNotEmpty
            .allSatisfy { product ->
                assertThat(product.category)
                    .isEqualToIgnoringCase(category)
            }
    }

    // ─── POST Create Product ───────────────────────────────────────────────────

    @Test
    @Order(8)
    @Story("Create Product")
    @Severity(SeverityLevel.CRITICAL)
    @Description("POST /products creates a new product and returns it with an id")
    fun `create product returns new product with id`() {
        val newProduct = TestDataFactory.randomProduct()

        val response = Given {
            spec(requestSpec)
            body(newProduct)
        } When {
            post("/products")
        } Then {
            statusCode(200)
        } Extract {
            `as`(ProductResponse::class.java)
        }

        createdProductId = response.id

        assertThat(response.id).isPositive()
        assertThat(response.title).isEqualTo(newProduct.title)
        assertThat(response.price).isEqualTo(newProduct.price)
        assertThat(response.category).isEqualTo(newProduct.category)
    }

    // ─── PUT Update Product ────────────────────────────────────────────────────

    @Test
    @Order(9)
    @Story("Update Product")
    @Severity(SeverityLevel.NORMAL)
    @Description("PUT /products/{id} updates the product")
    fun `update product returns updated data`() {
        val updatedProduct = TestDataFactory.randomProduct().copy(
            title = "UPDATED: Special Edition Product"
        )

        val response = Given {
            spec(requestSpec)
            body(updatedProduct)
        } When {
            put("/products/1")
        } Then {
            statusCode(200)
        } Extract {
            `as`(ProductResponse::class.java)
        }

        assertThat(response.title).isEqualTo(updatedProduct.title)
    }

    // ─── DELETE Product ────────────────────────────────────────────────────────

    @Test
    @Order(10)
    @Story("Delete Product")
    @Severity(SeverityLevel.NORMAL)
    @Description("DELETE /products/{id} removes the product")
    fun `delete product returns 200`() {
        Given {
            spec(requestSpec)
        } When {
            delete("/products/1")
        } Then {
            statusCode(200)
        }
    }
}
