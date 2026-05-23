package com.portfolio.tests.pages

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.PageFactory
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

/**
 * Base class for all Page Objects.
 *
 * Page Object Model (POM) is a design pattern that:
 * - Separates test logic from UI interactions
 * - Makes tests readable ("loginPage.enterEmail()" vs "driver.findElement(By.id(...)).sendKeys(...)")
 * - Centralizes selectors — one change fixes all tests
 * - Reduces code duplication
 *
 * This is what separates SDET-level work from basic automation.
 */
abstract class BasePage(protected val driver: WebDriver) {

    protected val wait = WebDriverWait(driver, Duration.ofSeconds(15))

    init {
        PageFactory.initElements(driver, this)
    }

    // ─── Common Actions ────────────────────────────────────────────────────────

    protected fun click(element: WebElement) {
        wait.until(ExpectedConditions.elementToBeClickable(element))
        element.click()
    }

    protected fun type(element: WebElement, text: String) {
        wait.until(ExpectedConditions.visibilityOf(element))
        element.clear()
        element.sendKeys(text)
    }

    protected fun waitForText(locator: By, text: String): WebElement =
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text))
            .let { driver.findElement(locator) }

    protected fun waitForVisible(locator: By): WebElement =
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator))

    protected fun isElementPresent(locator: By): Boolean = try {
        driver.findElement(locator).isDisplayed
    } catch (e: Exception) {
        false
    }

    protected fun getCurrentUrl(): String = driver.currentUrl

    protected fun getPageTitle(): String = driver.title
}
