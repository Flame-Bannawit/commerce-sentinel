package com.portfolio.tests.utils

import java.util.Properties

/**
 * Central config loader — reads test.properties and allows ENV override.
 * ENV variables take precedence over properties file (for CI/CD).
 *
 * Usage:
 *   val baseUrl = Config.get("api.base.url")
 *   val browser = Config.get("browser", "chrome")
 */
object Config {

    private val props = Properties()

    init {
        val stream = Config::class.java.classLoader.getResourceAsStream("test.properties")
            ?: error("test.properties not found in resources")
        props.load(stream)
    }

    /**
     * Get a config value. ENV variables override properties file.
     * e.g. api.base.url → API_BASE_URL env var
     */
    fun get(key: String, default: String? = null): String {
        val envKey = key.uppercase().replace(".", "_")
        return System.getenv(envKey)
            ?: props.getProperty(key)
            ?: default
            ?: error("Config key '$key' not found and no default provided")
    }

    fun getInt(key: String, default: Int = 0): Int = get(key, default.toString()).toInt()

    fun getBoolean(key: String, default: Boolean = false): Boolean =
        get(key, default.toString()).toBoolean()

    val apiBaseUrl: String get() = get("api.base.url")
    val apiTimeout: Int get() = getInt("api.timeout.seconds", 10)
    val browser: String get() = get("browser", "chrome")
    val headless: Boolean get() = getBoolean("browser.headless", true)
    val browserTimeout: Int get() = getInt("browser.timeout.seconds", 15)
}
