# Flipkart UI Automation Framework

[![CI Tests](https://github.com/Shreyans1999/FlipkartTesting/actions/workflows/test.yml/badge.svg)](https://github.com/Shreyans1999/FlipkartTesting/actions/workflows/test.yml)

A robust, scalable UI test automation framework for Flipkart e-commerce platform built with Java, Selenium WebDriver, and TestNG.

---

## Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21 | Programming Language |
| Selenium WebDriver | 4.38.0 | Browser Automation |
| TestNG | 7.11.0 | Test Framework |
| WebDriverManager | 6.3.3 | Driver Management |
| Log4j 2 | 2.25.2 | Logging |
| **Allure Reports** | **2.29.1** | **Interactive Test Reports** |
| Apache POI | 5.5.1 | Excel Data Handling |
| Maven | 3.x | Build & Dependency Management |


---

## Framework Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         TEST LAYER                              │
│   ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│   │ e2e Tests    │  │ Regression   │  │ Authentication Tests │  │
│   │ (7 classes)  │  │ (1 class)    │  │ (1 class)            │  │
│   └──────────────┘  └──────────────┘  └──────────────────────┘  │
│                              │                                   │
│                      ┌───────▼───────┐                          │
│                      │   BaseTest    │                          │
└──────────────────────┼───────────────┼──────────────────────────┘
                       │               │
┌──────────────────────┼───────────────┼──────────────────────────┐
│                      ▼               ▼              CORE LAYER  │
│   ┌─────────────────────┐  ┌─────────────────────┐              │
│   │   DriverFactory     │  │   ConfigManager     │              │
│   │ (Thread-safe driver)│  │    (Singleton)      │              │
│   └─────────────────────┘  └─────────────────────┘              │
│                                                                  │
│   │    TestListener     │  │   POJO Models       │              │
│   │ (Allure Screenshots) │  │ (4 data classes)    │              │
│   │ (ExtentReports)     │  │ (4 data classes)    │              │
│   └─────────────────────┘  └─────────────────────┘              │
└─────────────────────────────────────────────────────────────────┘
                               │
┌──────────────────────────────┼──────────────────────────────────┐
│                              ▼               PAGE OBJECT LAYER  │
│   ┌─────────────────────────────────────────────────────────┐   │
│   │                       BasePage                          │   │
│   │  (Common methods: waits, clicks, scrolls, screenshots)  │   │
│   └─────────────────────────────────────────────────────────┘   │
│                              │                                   │
│   ┌────────────┬────────────┬────────────┬────────────────────┐ │
│   │ LoginPage  │ SearchBox  │ FlightPage │ ... (21 Page       │ │
│   │ Register   │ Filters    │ PlaceOrder │      Objects)      │ │
│   └────────────┴────────────┴────────────┴────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

---

## Project Structure

```
FlipkartTesting/
├── pom.xml                          # Maven configuration
├── testng.xml                       # E2E test suite (no login required)
├── testng-regression.xml            # Regression suite (login required)
│
├── src/main/java/com/flipkart/
│   ├── core/
│   │   ├── config/
│   │   │   └── ConfigManager.java       # Singleton configuration loader
│   │   ├── driver/
│   │   │   └── DriverFactory.java       # Thread-safe WebDriver factory
│   │   └── listeners/
│   │       └── TestListener.java        # TestNG listener for reporting
│   │
│   ├── models/                          # POJO data classes (Builder pattern)
│   │   ├── AddressData.java
│   │   ├── FlightData.java
│   │   ├── ProductData.java
│   │   └── UserData.java
│   │
│   ├── pages/                           # Page Object classes (21 total)
│   │   ├── BasePage.java                # Abstract base with common methods
│   │   ├── FlipkartLoginPage.java
│   │   ├── FlipkartSearchBox.java
│   │   ├── FlipkartPlaceOrder.java
│   │   ├── FlipkartFlightPage.java
│   │   └── ... (17 more page objects)
│   │
│   └── utils/
│       ├── ExcelReader.java             # Apache POI Excel utilities
│       ├── ScreenshotUtils.java         # Screenshot capture
│       └── WindowUtils.java             # Browser window handling
│
├── src/main/resources/
│   └── config/
│       └── config.properties            # Environment configuration
│
├── src/test/java/com/flipkart/tests/
│   ├── base/
│   │   └── BaseTest.java                # Abstract test base class
│   ├── authentication/
│   │   └── AuthenticationTests.java     # Login/Register tests
│   ├── e2e/
│   │   ├── SiteLaunchTests.java
│   │   ├── ProductSearchTests.java
│   │   ├── ProductFilterTests.java
│   │   ├── CategoryBrowsingTests.java
│   │   ├── ProductDetailsTests.java
│   │   ├── FlightBookingTests.java
│   │   └── LocationTests.java
│   └── regression/
│       └── CartAndOrderTests.java       # Login-required order tests
│
├── reports/                             # Allure reports output
└── screenshots/                         # Test failure screenshots
```

---

## Prerequisites

- **Java JDK 21** or higher
- **Maven 3.x**
- **Chrome/Firefox/Edge** browser installed
- **IDE**: Eclipse, IntelliJ IDEA, or VS Code

---

## Setup Instructions

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd FlipkartTesting
   ```

2. **Install dependencies**
   ```bash
   mvn clean install -DskipTests
   ```

3. **Verify setup**
   ```bash
   mvn compile test-compile
   ```

---

## How to Run Tests

### Run All E2E Tests (No Login Required)
```bash
mvn test
```

### Run with Specific TestNG Suite
```bash
# E2E tests only
mvn test -DsuiteXmlFile=testng.xml

# Regression tests (requires manual OTP entry)
mvn test -DsuiteXmlFile=testng-regression.xml
```

### Run Specific Test Class
```bash
mvn test -Dtest=com.flipkart.tests.e2e.SiteLaunchTests
mvn test -Dtest=com.flipkart.tests.e2e.ProductSearchTests
```

### Run with Different Browser
```bash
mvn test -Dbrowser=firefox
mvn test -Dbrowser=edge
mvn test -Dbrowser=headless
```

### Parallel Execution
Configured in `pom.xml` with:
- Thread count: 4
- Parallel mode: tests

---

## Test Configuration

### config.properties
Located at `src/main/resources/config/config.properties`:

```properties
# Base URL
url=https://www.flipkart.com/

# Authentication
PhoneNo=9024002784
loginURL=https://www.flipkart.com/account/login

# Product Search
product=IPhone 17 Pro

# Location
pincode=560022

# Flight Booking
departureCity=Mumbai
arrivalCity=Delhi
```

### Supported Browsers
| Browser | Flag |
|---------|------|
| Chrome | `chrome` (default) |
| Firefox | `firefox` |
| Edge | `edge` |
| Chrome Headless | `headless` |
| Firefox Headless | `firefox-headless` |

---

## Reporting & Logging

### Allure Reports (Recommended)

Allure provides beautiful, interactive HTML reports with:
- Test execution timeline
- Step-by-step test breakdown  
- Screenshots on failure
- Environment information
- Test categorization

#### Prerequisites

Install Allure CLI (one-time setup):

**macOS:**
```bash
brew install allure
```

**Windows (via Scoop):**
```bash
scoop install allure
```

**Linux:**
```bash
sudo apt-get install allure
```

#### Generate & View Allure Report

```bash
# Step 1: Run E2E tests (generates allure-results)
mvn clean test

# Step 2: Generate and open report in browser
mvn allure:serve
```

This will:
1. Generate the HTML report from `target/allure-results`
2. Start a local web server
3. Open the report in your default browser

#### Alternative Commands

```bash
# Generate report without opening browser
mvn allure:report

# Report will be at: target/allure-report/index.html

# View existing report
allure open target/allure-report
```

#### Allure Report Structure

```
target/
├── allure-results/          # Raw test results (JSON)
└── allure-report/           # Generated HTML report
    └── index.html           # Open this in browser
```

> **Note:** Allure reports are generated only for E2E tests (`testng.xml`). Login-required tests in `testng-regression.xml` are excluded since they require manual OTP entry.

---

### Log4j 2

- Logger configured at class level
- Log levels: INFO, WARN, ERROR, DEBUG
- Usage in tests: `logger.info("Test step description")`

### Screenshots

- Location: `screenshots/` directory
- Captured on test failures
- Utility: `ScreenshotUtils.java`

---

## Test Suites

| Suite File | Description | Login Required |
|------------|-------------|----------------|
| `testng.xml` | E2E tests (Site, Search, Filters, Flights, etc.) | ❌ No |
| `testng-regression.xml` | Cart & Order tests | ✅ Yes (OTP) |

### Test Groups
| Group | Tests |
|-------|-------|
| `e2e` | All E2E functional tests |
| `regression` | Order and cart tests |
| `authentication` | Login/Register tests |
| `filter` | Product filtering tests |
| `search` | Product search tests |

---

## Best Practices Followed

- ✅ **Page Object Model (POM)** - Separation of page elements and test logic
- ✅ **Thread-safe Driver** - ThreadLocal for parallel execution support
- ✅ **Singleton Pattern** - ConfigManager for centralized configuration
- ✅ **Builder Pattern** - POJO models with fluent API
- ✅ **Base Classes** - BasePage and BaseTest for code reusability
- ✅ **Explicit Waits** - WebDriverWait over implicit waits
- ✅ **Fallback Locators** - Multiple XPath strategies for robustness
- ✅ **Centralized Configuration** - External properties file
- ✅ **Automatic Driver Management** - WebDriverManager handles binaries
- ✅ **TestNG Listeners** - Automated reporting and screenshots

---

## Key Features

| Feature | Implementation |
|---------|----------------|
| Multi-browser Support | Chrome, Firefox, Edge, Headless modes |
| Parallel Execution | ThreadLocal driver, 4 threads |
| Screenshot on Failure | TestListener auto-capture |
| **Allure Reports** | **Interactive HTML with timeline & steps** |
| **Allure Reports** | **Interactive HTML with timeline & steps** |
| Data-Driven Ready | Apache POI for Excel support |
| Configurable | External config.properties |
| Login Flow | Manual OTP entry support |

---

## Future Enhancements

> *Optional improvements not yet implemented:*

- [ ] Data-driven testing with Excel/CSV
- [ ] Docker containerization
- [ ] CI/CD pipeline (Jenkins/GitHub Actions)
- [ ] API testing integration
- [ ] Cross-browser grid (Selenium Grid)
- [ ] BDD with Cucumber integration

---

## Author

**Flipkart Testing Framework** - QA Automation Project

---

## License

This project is for educational and testing purposes.
