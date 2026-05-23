package com.portfolio.tests.e2e

import com.portfolio.tests.pages.*
import com.portfolio.tests.utils.Config
import com.portfolio.tests.utils.E2ETestBase
import com.portfolio.tests.utils.TestDataFactory
import io.qameta.allure.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*

/**
 * E2E Tests: Full User Journeys
 *
 * Tests the complete user experience from a browser perspective.
 * Uses Page Object Model (POM) — no selectors in test code.
 *
 * These tests simulate what a real user does:
 * 1. Register / Login
 * 2. Search for products
 * 3. Add to cart
 * 4. Checkout
 */
@Epic("E2E")
@Feature("User Journey")
@Tag("e2e")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UserJourneyE2ETest : E2ETestBase() {

    private val baseUrl = Config.apiBaseUrl.replace("/api", "").replace("fakestoreapi.com", "localhost:3000")
    private val testUser = TestDataFactory.randomUser()

    // ─── Authentication ────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @Story("Login")
    @Severity(SeverityLevel.BLOCKER)
    @Description("User can log in with valid credentials and is redirected to home page")
    fun `user can login with valid credentials`() {
        val homePage = LoginPage(driver)
            .navigate(baseUrl)
            .enterEmail("test@portfolio.dev")
            .enterPassword("Test@1234")
            .clickLogin()

        assertThat(homePage.isLoggedIn())
            .withFailMessage("Expected user to be logged in but user menu was not visible")
            .isTrue()
        assertThat(driver.currentUrl).contains("/home")
    }

    @Test
    @Order(2)
    @Story("Login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Login with wrong password shows error message")
    fun `login with wrong password shows error`() {
        val loginPage = LoginPage(driver)
            .navigate(baseUrl)
            .enterEmail("test@portfolio.dev")
            .enterPassword("WrongPassword!!")
            .clickLoginExpectingError()

        assertThat(loginPage.isErrorDisplayed())
            .withFailMessage("Expected error message to be shown for invalid credentials")
            .isTrue()
        assertThat(loginPage.getErrorMessage())
            .isNotBlank()
            .containsIgnoringCase("invalid")
    }

    @Test
    @Order(3)
    @Story("Registration")
    @Severity(SeverityLevel.CRITICAL)
    @Description("New user can register and is redirected to login page")
    fun `new user can register successfully`() {
        val loginPage = LoginPage(driver)
            .navigate(baseUrl)
            .clickRegister()
            .fillForm(
                firstname = testUser.name.firstname,
                lastname = testUser.name.lastname,
                email = testUser.email,
                password = testUser.password
            )
            .submit()

        // After successful registration, should be on login page
        assertThat(driver.currentUrl).contains("/login")
    }

    // ─── Product Search ────────────────────────────────────────────────────────

    @Test
    @Order(4)
    @Story("Product Search")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Searching for an existing product shows relevant results")
    fun `searching for existing product shows results`() {
        LoginPage(driver).navigate(baseUrl)
            .enterEmail("test@portfolio.dev")
            .enterPassword("Test@1234")
            .clickLogin()

        val searchResults = HomePage(driver).searchFor("laptop")

        assertThat(searchResults.hasResults())
            .withFailMessage("Expected search results for 'laptop' but got none")
            .isTrue()
    }

    @Test
    @Order(5)
    @Story("Product Search")
    @Severity(SeverityLevel.NORMAL)
    @Description("Searching for non-existent product shows empty state")
    fun `searching for non-existent product shows empty state`() {
        LoginPage(driver).navigate(baseUrl)
            .enterEmail("test@portfolio.dev")
            .enterPassword("Test@1234")
            .clickLogin()

        val searchResults = HomePage(driver).searchFor("xyzproductthatdoesnotexist12345")

        assertThat(searchResults.showsNoResults())
            .withFailMessage("Expected 'no results' message for invalid search term")
            .isTrue()
    }

    // ─── Add to Cart ───────────────────────────────────────────────────────────

    @Test
    @Order(6)
    @Story("Shopping Cart")
    @Severity(SeverityLevel.BLOCKER)
    @Description("User can add an in-stock product to cart")
    fun `user can add product to cart`() {
        LoginPage(driver).navigate(baseUrl)
            .enterEmail("test@portfolio.dev")
            .enterPassword("Test@1234")
            .clickLogin()

        val homePage = HomePage(driver)
        val cartCountBefore = homePage.getCartCount()

        homePage.searchFor("headphones")
            .clickFirstProduct()
            .addToCart()

        val cartCountAfter = HomePage(driver).getCartCount()
        assertThat(cartCountAfter).isEqualTo(cartCountBefore + 1)
    }

    @Test
    @Order(7)
    @Story("Shopping Cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Out-of-stock product shows disabled Add to Cart button")
    fun `out-of-stock product has disabled add to cart`() {
        LoginPage(driver).navigate(baseUrl)
            .enterEmail("test@portfolio.dev")
            .enterPassword("Test@1234")
            .clickLogin()

        // Navigate directly to the out-of-stock product
        driver.get("$baseUrl/products/4")
        val productPage = ProductPage(driver)

        assertThat(productPage.isOutOfStock())
            .withFailMessage("Expected out-of-stock label for product with stock=0")
            .isTrue()
        assertThat(productPage.isAddToCartEnabled())
            .withFailMessage("Expected Add to Cart button to be disabled for out-of-stock item")
            .isFalse()
    }

    // ─── Full Checkout Journey ─────────────────────────────────────────────────

    @Test
    @Order(8)
    @Story("Checkout")
    @Severity(SeverityLevel.BLOCKER)
    @Description("User can complete full checkout journey: Login → Search → Cart → Order")
    fun `user can complete full checkout journey`() {
        // Step 1: Login
        val homePage = LoginPage(driver)
            .navigate(baseUrl)
            .enterEmail("test@portfolio.dev")
            .enterPassword("Test@1234")
            .clickLogin()

        assertThat(homePage.isLoggedIn()).isTrue()

        // Step 2: Search & Select Product
        val productPage = homePage
            .searchFor("laptop")
            .clickFirstProduct()

        assertThat(productPage.getTitle()).isNotBlank()
        assertThat(productPage.getPrice()).isNotBlank()

        // Step 3: Add to Cart
        productPage.addToCart()

        // Step 4: Go to Cart
        val cartPage = HomePage(driver).goToCart()
        assertThat(cartPage.getItemCount()).isGreaterThan(0)
        assertThat(cartPage.isEmpty()).isFalse()

        // Step 5: Checkout
        val checkoutPage = cartPage.proceedToCheckout()
        assertThat(checkoutPage.isOrderSummaryVisible()).isTrue()

        // Step 6: Place Order
        checkoutPage.placeOrder()
        assertThat(checkoutPage.isOrderSuccessful())
            .withFailMessage("Expected order success message after checkout")
            .isTrue()
    }
}
