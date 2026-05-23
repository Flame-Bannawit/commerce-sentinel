package com.portfolio.tests.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

// ─── Auth ─────────────────────────────────────────────────────────────────────

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String
)

// ─── User ─────────────────────────────────────────────────────────────────────

data class UserRequest(
    val email: String,
    val username: String,
    val password: String,
    val name: Name,
    val phone: String,
    val address: Address
)

data class Name(
    val firstname: String,
    val lastname: String
)

data class Address(
    val city: String,
    val street: String,
    val number: Int,
    val zipcode: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserResponse(
    val id: Int,
    val email: String,
    val username: String,
    val name: Name,
    val phone: String,
    val address: Address
)

// ─── Product ──────────────────────────────────────────────────────────────────

data class ProductRequest(
    val title: String,
    val price: Double,
    val description: String,
    val image: String,
    val category: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductResponse(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val rating: Rating? = null
)

data class Rating(
    val rate: Double,
    val count: Int
)

// ─── Cart / Order ─────────────────────────────────────────────────────────────

data class CartRequest(
    val userId: Int,
    val date: String,
    val products: List<CartProduct>
)

data class CartProduct(
    val productId: Int,
    val quantity: Int
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CartResponse(
    val id: Int,
    val userId: Int,
    val date: String,
    val products: List<CartProduct>
)
