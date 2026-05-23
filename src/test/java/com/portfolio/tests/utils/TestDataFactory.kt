package com.portfolio.tests.utils

import com.github.javafaker.Faker
import com.portfolio.tests.models.*

/**
 * Centralized test data factory using JavaFaker.
 * Ensures tests never use hardcoded/shared data — each test gets fresh data.
 *
 * Usage:
 *   val user = TestDataFactory.randomUser()
 *   val order = TestDataFactory.randomOrder(productId = 3)
 */
object TestDataFactory {

    private val faker = Faker()

    // ─── Users ────────────────────────────────────────────────────────────────

    fun randomUser(
        email: String = faker.internet().emailAddress(),
        password: String = "Test@${faker.number().digits(4)}",
        username: String = faker.name().username()
    ) = UserRequest(
        email = email,
        password = password,
        username = username,
        name = Name(
            firstname = faker.name().firstName(),
            lastname = faker.name().lastName()
        ),
        phone = faker.phoneNumber().phoneNumber(),
        address = Address(
            city = faker.address().city(),
            street = faker.address().streetName(),
            number = faker.number().numberBetween(1, 999),
            zipcode = faker.address().zipCode()
        )
    )

    fun loginRequest(
        username: String = "mor_2314",  // FakeStore seeded user
        password: String = "83r5^_"
    ) = LoginRequest(username, password)

    // ─── Products ─────────────────────────────────────────────────────────────

    fun randomProduct() = ProductRequest(
        title = faker.commerce().productName(),
        price = faker.number().randomDouble(2, 1, 500),
        description = faker.lorem().sentence(),
        image = "https://fakestoreapi.com/img/placeholder.jpg",
        category = listOf("electronics", "jewelery", "men's clothing", "women's clothing").random()
    )

    // ─── Carts / Orders ───────────────────────────────────────────────────────

    fun randomCart(
        userId: Int = faker.number().numberBetween(1, 10),
        productId: Int = faker.number().numberBetween(1, 20),
        quantity: Int = faker.number().numberBetween(1, 5)
    ) = CartRequest(
        userId = userId,
        date = "2024-01-01",
        products = listOf(CartProduct(productId = productId, quantity = quantity))
    )
}
