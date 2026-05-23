plugins {
    kotlin("jvm") version "1.9.22"
    id("io.qameta.allure") version "2.11.2"
}

group = "com.portfolio"
version = "1.0.0"

repositories {
    mavenCentral()
}

val allureVersion = "2.25.0"
val restAssuredVersion = "5.4.0"
val seleniumVersion = "4.18.1"
val testcontainersVersion = "1.19.6"
val junitVersion = "5.10.2"

dependencies {
    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

    // REST Assured — API Testing
    testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")
    testImplementation("io.rest-assured:json-schema-validator:$restAssuredVersion")
    testImplementation("io.rest-assured:kotlin-extensions:$restAssuredVersion")

    // Selenium — E2E Testing
    testImplementation("org.seleniumhq.selenium:selenium-java:$seleniumVersion")
    testImplementation("io.github.bonigarcia:webdrivermanager:5.7.0")

    // Testcontainers — Integration Testing
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")

    // Spring Boot Test — Integration
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.3")
    testImplementation("org.springframework.boot:spring-boot-starter-web:3.2.3")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.3")
    testRuntimeOnly("org.postgresql:postgresql:42.7.2")

    // Allure — Reporting
    testImplementation("io.qameta.allure:allure-junit5:$allureVersion")
    testImplementation("io.qameta.allure:allure-rest-assured:$allureVersion")
    testImplementation("io.qameta.allure:allure-selenide:$allureVersion")

    // Utilities
    testImplementation("com.github.javafaker:javafaker:1.0.2")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    testImplementation("ch.qos.logback:logback-classic:1.4.14")
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
    systemProperty("allure.results.directory", "$projectDir/allure-results")

    // Tag-based test filtering
    // Run: ./gradlew test -Dtags="api"  OR  -Dtags="e2e"  OR  -Dtags="integration"
    val tags = System.getProperty("tags")
    if (tags != null) {
        filter {
            includeTestsMatching("*")
        }
        systemProperty("junit.jupiter.extensions.autodetection.enabled", "true")
        jvmArgs("-Dtags=$tags")
    }

    testLogging {
        events("passed", "skipped", "failed")
        showExceptions = true
        showCauses = true
        showStackTraces = false
    }
}

allure {
    report {
        version.set(allureVersion)
    }
    adapter {
        allureJavaVersion.set(allureVersion)
    }
}
