package com.portfolio.tests.utils

import io.github.bonigarcia.wdm.WebDriverManager
import io.qameta.allure.Attachment
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

/**
 * Base class for all E2E Selenium tests.
 *
 * Handles:
 * - WebDriver lifecycle (setup/teardown)
 * - Headless mode (for CI)
 * - Screenshot on failure (attached to Allure report)
 * - Explicit wait configuration
 */
@Tag("e2e")
abstract class E2ETestBase {

    protected lateinit var driver: WebDriver
    protected lateinit var wait: WebDriverWait

    @BeforeEach
    fun setUpDriver() {
        driver = when (Config.browser.lowercase()) {
            "firefox" -> {
                WebDriverManager.firefoxdriver().setup()
                val options = FirefoxOptions().apply {
                    if (Config.headless) addArguments("--headless")
                }
                FirefoxDriver(options)
            }
            else -> {
                WebDriverManager.chromedriver().setup()
                val options = ChromeOptions().apply {
                    if (Config.headless) addArguments("--headless=new")
                    addArguments("--no-sandbox")
                    addArguments("--disable-dev-shm-usage")
                    addArguments("--window-size=1920,1080")
                    addArguments("--disable-gpu")
                }
                ChromeDriver(options)
            }
        }

        wait = WebDriverWait(driver, Duration.ofSeconds(Config.browserTimeout.toLong()))
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5))
    }

    @AfterEach
    fun tearDownDriver() {
        takeScreenshot()   // Always screenshot — attached to Allure report
        driver.quit()
    }

    @Attachment(value = "Screenshot", type = "image/png")
    fun takeScreenshot(): ByteArray? {
        return try {
            (driver as TakesScreenshot).getScreenshotAs(OutputType.BYTES)
        } catch (e: Exception) {
            null
        }
    }
}
