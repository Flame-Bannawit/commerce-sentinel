# 🧪 E-Commerce Automated Test Suite

![CI](https://github.com/YOUR_USERNAME/ecommerce-test-suite/actions/workflows/test-suite.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-17-orange?logo=java)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9-purple?logo=kotlin)
![JUnit5](https://img.shields.io/badge/JUnit-5-green)
![Allure](https://img.shields.io/badge/Report-Allure-orange)

> A production-grade automated test suite demonstrating QA engineering skills across API, Integration, and E2E testing layers using Java/Kotlin ecosystem.

📊 **[Live Allure Report →](https://YOUR_USERNAME.github.io/ecommerce-test-suite)**

---

## 🏗️ Architecture

```
ecommerce-test-suite/
├── src/test/java/com/portfolio/tests/
│   ├── api/                    # REST Assured — API Contract Tests
│   │   ├── AuthApiTest.kt      # Login, token validation
│   │   ├── ProductApiTest.kt   # CRUD + filtering + sorting
│   │   └── CartApiTest.kt      # Cart management + date filters
│   │
│   ├── integration/            # Testcontainers — Real DB Tests
│   │   ├── OrderServiceIntegrationTest.kt  # Business logic + rollback
│   │   └── IntegrationTestBase.kt          # PostgreSQL container setup
│   │
│   ├── e2e/                    # Selenium — Browser Journey Tests
│   │   └── UserJourneyE2ETest.kt  # Full user flow (login→search→cart→order)
│   │
│   ├── pages/                  # Page Object Model
│   │   ├── BasePage.kt         # Shared WebDriver helpers
│   │   └── Pages.kt            # LoginPage, HomePage, CartPage, etc.
│   │
│   ├── models/                 # Request/Response POJOs
│   │   └── Models.kt
│   │
│   └── utils/                  # Shared Utilities
│       ├── Config.kt           # Centralized config (ENV override support)
│       ├── TestDataFactory.kt  # JavaFaker-based test data generation
│       ├── ApiTestBase.kt      # RestAssured + Allure filter setup
│       └── E2ETestBase.kt      # WebDriver lifecycle + screenshot
│
├── src/test/resources/
│   ├── test.properties         # Configuration
│   ├── logback-test.xml        # Logging config
│   └── db/init.sql             # DB schema + seed data
│
├── .github/workflows/
│   └── test-suite.yml          # CI/CD — runs all 3 test layers
│
└── build.gradle.kts            # Gradle Kotlin DSL
```

---

## 🛠️ Tech Stack

| Layer | Tools | Purpose |
|---|---|---|
| **API Testing** | REST Assured 5 + JUnit 5 | Contract testing, CRUD validation |
| **Integration** | Testcontainers + PostgreSQL | Real DB tests, business logic, rollback |
| **E2E** | Selenium 4 + WebDriverManager | Full user journey browser tests |
| **Reporting** | Allure Report 2 | Rich HTML reports with screenshots |
| **CI/CD** | GitHub Actions | Auto-run on push + PR + schedule |
| **Test Data** | JavaFaker | Dynamic, unique data per test run |
| **Language** | Kotlin (Gradle DSL) | Concise, null-safe, modern JVM |

---

## 🚀 Quick Start

### Prerequisites
- JDK 17+
- Chrome browser (for E2E tests)
- Docker (for Integration tests with Testcontainers)

### Run All Tests
```bash
./gradlew test
```

### Run by Layer
```bash
# API Tests only
./gradlew test -Dtags="api"

# Integration Tests only (requires Docker)
./gradlew test -Dtags="integration"

# E2E Tests only
./gradlew test -Dtags="e2e"
```

### Generate Allure Report
```bash
./gradlew allureServe
```

### Run with custom API URL
```bash
API_BASE_URL=https://your-api.com ./gradlew test -Dtags="api"
```

---

## 🧠 Design Patterns & Best Practices

### 1. Page Object Model (POM)
All E2E tests use POM — zero selectors in test code. Every page interaction is encapsulated in a Page class, making tests readable and maintainable.

```kotlin
// ✅ What our tests look like (readable, maintainable)
LoginPage(driver)
    .navigate(baseUrl)
    .enterEmail("test@example.com")
    .enterPassword("secret")
    .clickLogin()

// ❌ What tests look like WITHOUT POM (brittle, unreadable)
driver.findElement(By.id("email")).sendKeys("test@example.com")
driver.findElement(By.id("password")).sendKeys("secret")
driver.findElement(By.cssSelector("button[type='submit']")).click()
```

### 2. Testcontainers for Real DB Integration
Integration tests run against a real PostgreSQL instance — not H2, not mocks. This catches issues that in-memory DBs would miss (constraint violations, transaction rollbacks, SQL dialect differences).

### 3. Test Data Factory
Every test generates fresh, unique data using JavaFaker — no shared state, no test pollution.

### 4. ENV Override Pattern
All config values can be overridden via environment variables, making the suite CI/CD-friendly.

| Property | ENV Variable |
|---|---|
| `api.base.url` | `API_BASE_URL` |
| `browser` | `BROWSER` |
| `browser.headless` | `BROWSER_HEADLESS` |

### 5. Allure Annotations
Every test uses `@Epic`, `@Feature`, `@Story`, `@Severity`, `@Description` for rich, categorized reports.

---

## 📊 Test Coverage

| Area | Tests | Coverage |
|---|---|---|
| Authentication | 6 | Login happy/negative, parameterized invalid combos |
| Products API | 10 | CRUD, pagination, sorting, category filter |
| Cart API | 4 | List, filter by user, add, date range |
| Order Integration | 6 | Place order, stock deduction, rollback, total calc |
| User Journey E2E | 8 | Login, register, search, add-to-cart, checkout |
| **Total** | **34** | |

---

## 🔄 CI/CD Pipeline

```
Push / PR → API Tests ─┐
                        ├─→ Allure Report → GitHub Pages
           Integration ─┤
                        │
           E2E Tests ───┘
```

- API and E2E tests run in parallel
- Integration tests provision PostgreSQL via GitHub Actions `services`
- Report published to GitHub Pages on every `main` push
- PR gets auto-commented with report link
- Scheduled daily run at 02:00 UTC

---

## 👤 Author

Flame-Bannawit — QA Engineer  
[LinkedIn](https://www.linkedin.com/in/bannawit-chaichomphu-61094b3b4) · [GitHub](https://github.com/Flame-Bannawit)
