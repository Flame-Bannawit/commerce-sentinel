package com.portfolio.tests.pages

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

// ─── Login Page ───────────────────────────────────────────────────────────────

class LoginPage(driver: WebDriver) : BasePage(driver) {

    @FindBy(id = "email")
    private lateinit var emailInput: WebElement

    @FindBy(id = "password")
    private lateinit var passwordInput: WebElement

    @FindBy(css = "button[type='submit']")
    private lateinit var loginButton: WebElement

    @FindBy(css = ".error-message, [data-testid='login-error']")
    private lateinit var errorMessage: WebElement

    @FindBy(css = "a[href='/register']")
    private lateinit var registerLink: WebElement

    fun navigate(baseUrl: String): LoginPage {
        driver.get("$baseUrl/login")
        return this
    }

    fun enterEmail(email: String): LoginPage {
        type(emailInput, email)
        return this
    }

    fun enterPassword(password: String): LoginPage {
        type(passwordInput, password)
        return this
    }

    fun clickLogin(): HomePage {
        click(loginButton)
        return HomePage(driver)
    }

    fun clickLoginExpectingError(): LoginPage {
        click(loginButton)
        return this
    }

    fun getErrorMessage(): String = errorMessage.text

    fun isErrorDisplayed(): Boolean = isElementPresent(
        org.openqa.selenium.By.cssSelector(".error-message, [data-testid='login-error']")
    )

    fun clickRegister(): RegisterPage {
        click(registerLink)
        return RegisterPage(driver)
    }
}

// ─── Register Page ────────────────────────────────────────────────────────────

class RegisterPage(driver: WebDriver) : BasePage(driver) {

    @FindBy(id = "firstname")
    private lateinit var firstnameInput: WebElement

    @FindBy(id = "lastname")
    private lateinit var lastnameInput: WebElement

    @FindBy(id = "email")
    private lateinit var emailInput: WebElement

    @FindBy(id = "password")
    private lateinit var passwordInput: WebElement

    @FindBy(id = "confirmPassword")
    private lateinit var confirmPasswordInput: WebElement

    @FindBy(css = "button[type='submit']")
    private lateinit var registerButton: WebElement

    fun fillForm(
        firstname: String,
        lastname: String,
        email: String,
        password: String
    ): RegisterPage {
        type(firstnameInput, firstname)
        type(lastnameInput, lastname)
        type(emailInput, email)
        type(passwordInput, password)
        type(confirmPasswordInput, password)
        return this
    }

    fun submit(): LoginPage {
        click(registerButton)
        return LoginPage(driver)
    }
}

// ─── Home Page ────────────────────────────────────────────────────────────────

class HomePage(driver: WebDriver) : BasePage(driver) {

    @FindBy(css = "[data-testid='user-menu'], .user-avatar")
    private lateinit var userMenu: WebElement

    @FindBy(css = "[data-testid='search-input'], input[placeholder*='Search']")
    private lateinit var searchInput: WebElement

    @FindBy(css = "[data-testid='cart-icon'], .cart-icon")
    private lateinit var cartIcon: WebElement

    @FindBy(css = "[data-testid='cart-count'], .cart-badge")
    private lateinit var cartCount: WebElement

    @FindBy(css = ".product-card, [data-testid='product-card']")
    private lateinit var productCards: WebElement

    fun isLoggedIn(): Boolean = isElementPresent(
        org.openqa.selenium.By.cssSelector("[data-testid='user-menu'], .user-avatar")
    )

    fun searchFor(query: String): SearchResultsPage {
        type(searchInput, query)
        searchInput.sendKeys(org.openqa.selenium.Keys.ENTER)
        return SearchResultsPage(driver)
    }

    fun goToCart(): CartPage {
        click(cartIcon)
        return CartPage(driver)
    }

    fun getCartCount(): Int {
        return try {
            cartCount.text.trim().toInt()
        } catch (e: Exception) {
            0
        }
    }
}

// ─── Search Results Page ──────────────────────────────────────────────────────

class SearchResultsPage(driver: WebDriver) : BasePage(driver) {

    @FindBy(css = ".product-card, [data-testid='product-card']")
    private lateinit var results: WebElement

    @FindBy(css = ".no-results, [data-testid='no-results']")
    private lateinit var noResults: WebElement

    @FindBy(css = ".results-count, [data-testid='results-count']")
    private lateinit var resultsCount: WebElement

    fun hasResults(): Boolean = isElementPresent(
        org.openqa.selenium.By.cssSelector(".product-card, [data-testid='product-card']")
    )

    fun showsNoResults(): Boolean = isElementPresent(
        org.openqa.selenium.By.cssSelector(".no-results, [data-testid='no-results']")
    )

    fun clickFirstProduct(): ProductPage {
        val firstProduct = driver.findElements(
            org.openqa.selenium.By.cssSelector(".product-card, [data-testid='product-card']")
        ).first()
        click(firstProduct)
        return ProductPage(driver)
    }
}

// ─── Product Detail Page ──────────────────────────────────────────────────────

class ProductPage(driver: WebDriver) : BasePage(driver) {

    @FindBy(css = ".product-title, [data-testid='product-title']")
    private lateinit var title: WebElement

    @FindBy(css = ".product-price, [data-testid='product-price']")
    private lateinit var price: WebElement

    @FindBy(css = ".product-description, [data-testid='product-description']")
    private lateinit var description: WebElement

    @FindBy(css = "[data-testid='add-to-cart'], .add-to-cart-btn")
    private lateinit var addToCartButton: WebElement

    @FindBy(css = "[data-testid='out-of-stock'], .out-of-stock-label")
    private lateinit var outOfStockLabel: WebElement

    @FindBy(css = "[data-testid='quantity-input'], input[name='quantity']")
    private lateinit var quantityInput: WebElement

    fun getTitle(): String = title.text
    fun getPrice(): String = price.text
    fun getDescription(): String = description.text

    fun isOutOfStock(): Boolean = isElementPresent(
        org.openqa.selenium.By.cssSelector("[data-testid='out-of-stock'], .out-of-stock-label")
    )

    fun setQuantity(qty: Int): ProductPage {
        type(quantityInput, qty.toString())
        return this
    }

    fun addToCart(): ProductPage {
        click(addToCartButton)
        return this
    }

    fun isAddToCartEnabled(): Boolean = addToCartButton.isEnabled
}

// ─── Cart Page ────────────────────────────────────────────────────────────────

class CartPage(driver: WebDriver) : BasePage(driver) {

    @FindBy(css = ".cart-item, [data-testid='cart-item']")
    private lateinit var cartItems: WebElement

    @FindBy(css = ".cart-total, [data-testid='cart-total']")
    private lateinit var cartTotal: WebElement

    @FindBy(css = "[data-testid='checkout-btn'], .checkout-button")
    private lateinit var checkoutButton: WebElement

    @FindBy(css = ".empty-cart, [data-testid='empty-cart']")
    private lateinit var emptyCart: WebElement

    fun isEmpty(): Boolean = isElementPresent(
        org.openqa.selenium.By.cssSelector(".empty-cart, [data-testid='empty-cart']")
    )

    fun getItemCount(): Int = driver.findElements(
        org.openqa.selenium.By.cssSelector(".cart-item, [data-testid='cart-item']")
    ).size

    fun getTotalText(): String = cartTotal.text

    fun proceedToCheckout(): CheckoutPage {
        click(checkoutButton)
        return CheckoutPage(driver)
    }
}

// ─── Checkout Page ────────────────────────────────────────────────────────────

class CheckoutPage(driver: WebDriver) : BasePage(driver) {

    @FindBy(css = "[data-testid='order-summary'], .order-summary")
    private lateinit var orderSummary: WebElement

    @FindBy(css = "[data-testid='place-order-btn'], .place-order-button")
    private lateinit var placeOrderButton: WebElement

    @FindBy(css = "[data-testid='order-success'], .order-success-message")
    private lateinit var successMessage: WebElement

    fun isOrderSummaryVisible(): Boolean = isElementPresent(
        org.openqa.selenium.By.cssSelector("[data-testid='order-summary'], .order-summary")
    )

    fun placeOrder(): CheckoutPage {
        click(placeOrderButton)
        return this
    }

    fun isOrderSuccessful(): Boolean = isElementPresent(
        org.openqa.selenium.By.cssSelector("[data-testid='order-success'], .order-success-message")
    )

    fun getSuccessMessage(): String = successMessage.text
}
