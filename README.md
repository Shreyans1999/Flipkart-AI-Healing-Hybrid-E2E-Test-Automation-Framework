# 🚀 Flipkart AI-Powered Self-Healing Hybrid E2E Test Automation Framework

> **Enterprise-Grade Test Automation with AI-Powered Self-Healing Locators**
>
> This framework combines the power of **Java/Selenium** for robust Flipkart UI testing with **TypeScript/Playwright AI** for intelligent locator self-healing. When selectors break, AI automatically fixes them!

[![Java CI](https://github.com/Shreyans1999/Flipkart-AI-Healing-Hybrid-E2E-Test-Automation-Framework/actions/workflows/test.yml/badge.svg)](https://github.com/Shreyans1999/Flipkart-AI-Healing-Hybrid-E2E-Test-Automation-Framework/actions)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/projects/jdk/21/)
[![Selenium](https://img.shields.io/badge/Selenium-4.38.0-green)](https://selenium.dev/)
[![Playwright](https://img.shields.io/badge/Playwright-1.40-blue)](https://playwright.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.3-blue)](https://www.typescriptlang.org/)

---

## 📌 What Makes This Special?

Traditional test automation breaks when UI changes. This framework **automatically heals broken selectors** using AI:

```
❌ Selector "#old-button" fails
   ↓
🔧 AI Healing Engine analyzes the page
   ↓
🤖 LLM (Ollama/OpenAI) suggests new selectors
   ↓
✅ Test continues with "button[type='submit']"
   ↓
📝 Locator file auto-updated for future runs
```

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           JAVA TEST FRAMEWORK                                │
│  ┌─────────────────────────────────────────────────────────────────────────┐│
│  │                          FlipkartTesting/                               ││
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────────┐  ││
│  │  │  TestNG      │  │  Page Objects│  │  SelfHealingBasePage         │  ││
│  │  │  Test Suite  │──│  (21 pages)  │──│  (smartClick, smartType...)  │  ││
│  │  └──────────────┘  └──────────────┘  └───────────────┬──────────────┘  ││
│  └─────────────────────────────────────────────────────│───────────────────┘│
│                                                        │                     │
│                                             HTTP REST API                    │
│                                                        │                     │
│  ┌─────────────────────────────────────────────────────│───────────────────┐│
│  │                  AI-SELF-HEALING-PLAYWRIGHT-FRAMEWORK                   ││
│  │  ┌─────────────────────────────────────────────────▼──────────────────┐ ││
│  │  │                    Healing API Server (Express.js)                 │ ││
│  │  │  POST /api/heal  →  LLM Analysis  →  Selector Generation          │ ││
│  │  └────────────────────────────────┬───────────────────────────────────┘ ││
│  │                                   │                                      ││
│  │  ┌───────────────┐  ┌─────────────▼─────────────┐  ┌──────────────────┐ ││
│  │  │ LLM Client    │  │    Healing Engine         │  │ Locator Analyzer │ ││
│  │  │ (Ollama/GPT)  │◄─│ (DOM analysis, fallbacks) │──│ (Prompt builder) │ ││
│  │  └───────────────┘  └───────────────────────────┘  └──────────────────┘ ││
│  └─────────────────────────────────────────────────────────────────────────┘│
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────────┐│
│  │                         shared/locators/                                ││
│  │            JSON locator files with primary + fallback selectors         ││
│  └─────────────────────────────────────────────────────────────────────────┘│
└──────────────────────────────────────────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Component | Technology | Version | Purpose |
|-----------|------------|---------|---------|
| **Java Framework** | Java | 21 | Main test language |
| | Selenium WebDriver | 4.38.0 | Browser automation |
| | TestNG | 7.11.0 | Test runner |
| | Gson | 2.10.1 | JSON parsing |
| **AI Healing Engine** | TypeScript | 5.3 | Healing service |
| | Playwright | 1.40 | DOM validation |
| | Express.js | 4.18 | REST API server |
| | Ollama | llama3.1 | Local LLM (free) |
| **Reporting** | Allure Reports | 2.29.1 | Rich test reports |
| | Log4j 2 | 2.25.2 | Java logging |
| | Winston | 3.11 | Node.js logging |

---

## 📁 Project Structure

```
Flipkart-AI-Healing-Hybrid-E2E-Test-Automation-Framework/
│
├── 📂 FlipkartTesting/                    # Java/Selenium Test Framework
│   ├── pom.xml                            # Maven dependencies
│   ├── testng.xml                         # E2E test suite
│   ├── testng-regression.xml              # Regression suite (login required)
│   │
│   ├── src/main/java/com/flipkart/
│   │   ├── core/
│   │   │   ├── config/ConfigManager.java      # Configuration loader
│   │   │   ├── driver/DriverFactory.java      # Thread-safe WebDriver
│   │   │   ├── listeners/TestListener.java    # Allure reporting
│   │   │   └── healing/                       # 🔧 Self-Healing Client
│   │   │       ├── HealingServiceClient.java  # HTTP client to healing API
│   │   │       ├── HealingConfig.java         # Healing configuration
│   │   │       ├── HealingRequest.java        # Request POJO
│   │   │       ├── HealingResult.java         # Response POJO
│   │   │       └── LocatorConfig.java         # JSON locator reader
│   │   │
│   │   └── pages/
│   │       ├── BasePage.java                  # Base page with common methods
│   │       ├── SelfHealingBasePage.java       # 🔧 Smart element methods
│   │       ├── FlipkartSelfHealingDemo.java   # Demo page object
│   │       └── ... (21 page objects)
│   │
│   └── src/test/java/com/flipkart/tests/
│       ├── base/BaseTest.java                 # Test base class
│       ├── e2e/                               # E2E tests (no login)
│       │   ├── SiteLaunchTests.java
│       │   ├── ProductSearchTests.java
│       │   ├── SelfHealingDemoTest.java       # 🔧 Healing demo test
│       │   └── ...
│       └── regression/                        # Regression tests (login)
│
├── 📂 AI-SELF-HEALING-PLAYWRIGHT-FRAMEWORK/  # TypeScript/Playwright AI Engine
│   ├── package.json
│   ├── .env                                   # LLM configuration
│   │
│   └── src/
│       ├── server/
│       │   └── server.ts                      # 🔧 Healing API Server
│       │
│       ├── ai/                                # AI Healing Engine
│       │   ├── HealingEngine.ts               # Main orchestrator
│       │   ├── LLMClient.ts                   # Ollama/OpenAI client
│       │   ├── LocatorAnalyzer.ts             # Prompt builder
│       │   └── HealingStrategy.ts             # Selector validation
│       │
│       ├── utils/
│       │   ├── Logger.ts                      # Winston logging
│       │   ├── FileUtils.ts                   # File operations
│       │   └── EnvLoader.ts                   # Environment config
│       │
│       └── locators/                          # Playwright locators
│
├── 📂 shared/                                 # Shared between frameworks
│   └── locators/
│       ├── flipkart-homepage.locators.json   # Homepage element locators
│       └── flipkart-search.locators.json     # Search page locators
│
├── TestingScenario.md                         # Self-healing demo guide
└── README.md                                  # This file
```

---

## 🚀 Quick Start

### Prerequisites

| Requirement | Command to Check | Install Guide |
|-------------|------------------|---------------|
| **Java 21** | `java -version` | [Download JDK 21](https://adoptium.net/) |
| **Maven 3.x** | `mvn -version` | [Install Maven](https://maven.apache.org/install.html) |
| **Node.js 18+** | `node -v` | [Install Node.js](https://nodejs.org/) |
| **Chrome Browser** | - | [Download Chrome](https://google.com/chrome) |
| **Ollama** (for AI) | `ollama --version` | [Install Ollama](https://ollama.ai/) |

### Step 1: Clone the Repository

```bash
git clone https://github.com/Shreyans1999/Flipkart-AI-Healing-Hybrid-E2E-Test-Automation-Framework.git
cd Flipkart-AI-Healing-Hybrid-E2E-Test-Automation-Framework
```

### Step 2: Install Dependencies

```bash
# Install Java dependencies
cd FlipkartTesting
mvn clean install -DskipTests

# Install Node.js dependencies
cd ../AI-SELF-HEALING-PLAYWRIGHT-FRAMEWORK
npm install
```

### Step 3: Configure Ollama (Local LLM - Free)

```bash
# Install Ollama (macOS)
brew install ollama

# Start Ollama server
ollama serve

# Pull the LLM model (in another terminal)
ollama pull llama3.1:8b
```

### Step 4: Configure Environment

Create `.env` file in `AI-SELF-HEALING-PLAYWRIGHT-FRAMEWORK/`:

```env
# LLM Provider Configuration
LLM_PROVIDER=ollama
OLLAMA_BASE_URL=http://localhost:11434
OLLAMA_MODEL=llama3.1:8b

# Self-Healing Settings
MAX_HEALING_RETRIES=3
HEALING_CONFIDENCE_THRESHOLD=0.7
AUTO_UPDATE_LOCATORS=true
```

---

## ▶️ Running Tests

### Quick Run (Without Self-Healing)

```bash
cd FlipkartTesting

# Run all E2E tests
mvn test

# Run specific test class
mvn test -Dtest=ProductSearchTests

# Run specific test method
mvn test -Dtest=SiteLaunchTests#verifyFlipkartHomepageTitleAndElements
```

### 🔧 Running with Self-Healing (Full Demo)

**Terminal 1: Start Ollama**
```bash
ollama serve
```

**Terminal 2: Start Healing API Server**
```bash
cd AI-SELF-HEALING-PLAYWRIGHT-FRAMEWORK
npm run server
```

You should see:
```
🚀 Healing API Server running on http://localhost:3001
   - Health: GET  /api/health
   - Heal:   POST /api/heal
```

**Terminal 3: Run Tests with Healing Enabled**
```bash
cd FlipkartTesting
mvn test -Dtest=SelfHealingDemoTest
```

---

## 🔧 Self-Healing Demo

To test the self-healing in action:

### 1. Break a Locator

Edit `shared/locators/flipkart-homepage.locators.json`:
```json
{
  "searchButton": {
    "primary": "#completely-broken-selector",
    "fallbacks": ["#also-broken-1", "#also-broken-2"]
  }
}
```

### 2. Run the Demo Test

```bash
# Make sure healing server is running
cd FlipkartTesting
mvn test -Dtest=SelfHealingDemoTest#testSearchButtonHealing
```

### 3. Watch the Magic Happen

**Server logs:**
```
🔧 Healing request for: flipkart-homepage.searchButton
   failedSelector: #completely-broken-selector
LLM returned 3 selectors
✅ Using selector: button[type='submit']
✅ Healing succeeded
```

**Locator file auto-updated:**
```json
{
  "searchButton": {
    "primary": "button[type='submit']",  // Healed!
    "fallbacks": ["#completely-broken-selector", ...],
    "lastHealed": "2025-12-29T...",
    "healCount": 1
  }
}
```

---

## 📊 Test Reports

### Generate Allure Reports

```bash
cd FlipkartTesting

# Run tests
mvn test

# Generate report
mvn allure:report

# Open report in browser
mvn allure:serve
```

Reports are saved to `FlipkartTesting/target/allure-report/`

---

## ⚙️ Configuration

### Java Framework (`config.properties`)

```properties
# FlipkartTesting/src/main/resources/config/config.properties

# Browser Configuration
browser=chrome
headless=false
implicitWait=10
pageLoadTimeout=30

# Test Environment
baseUrl=https://www.flipkart.com

# AI Self-Healing Configuration
healing.enabled=true
healing.serviceUrl=http://localhost:3001
healing.maxRetries=3
healing.confidenceThreshold=0.7
healing.locatorsDir=../shared/locators
```

### AI Healing Engine (`.env`)

```env
# AI-SELF-HEALING-PLAYWRIGHT-FRAMEWORK/.env

# Option 1: Ollama (Free, Local)
LLM_PROVIDER=ollama
OLLAMA_BASE_URL=http://localhost:11434
OLLAMA_MODEL=llama3.1:8b

# Option 2: OpenAI (Paid, Cloud)
# LLM_PROVIDER=openai
# OPENAI_API_KEY=sk-your-key-here
# OPENAI_MODEL=gpt-4
```

---

## 📖 API Endpoints

The Healing API Server exposes these endpoints:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/health` | GET | Health check |
| `/api/heal` | POST | Request selector healing |
| `/api/validate` | POST | Validate a selector |
| `/api/locators/:pageName` | GET | Get locators for a page |

### Example Heal Request

```bash
curl -X POST http://localhost:3001/api/heal \
  -H "Content-Type: application/json" \
  -d '{
    "pageName": "flipkart-homepage",
    "elementKey": "searchButton",
    "failedSelector": "#broken",
    "domSnapshot": "<html>...</html>",
    "action": "click"
  }'
```

---

## 🧪 Test Suites

| Suite | Command | Description |
|-------|---------|-------------|
| E2E Tests | `mvn test -DsuiteXmlFile=testng.xml` | Basic tests (no login) |
| Regression | `mvn test -DsuiteXmlFile=testng-regression.xml` | Full tests (login required) |
| Single Test | `mvn test -Dtest=TestClass#methodName` | Run specific test |
| Healing Demo | `mvn test -Dtest=SelfHealingDemoTest` | Self-healing demo |

---

## 📁 Shared Locator Format

```json
{
  "elementKey": {
    "primary": "main-selector",
    "fallbacks": ["fallback1", "fallback2"],
    "lastHealed": "ISO-timestamp",
    "healCount": 0
  }
}
```

Supported selector formats:
- CSS: `#id`, `.class`, `[attr='value']`
- XPath: `//tag[@attr='value']`
- Playwright: `text=Login`, `role=button`

---

## 🛡️ Best Practices

1. **Use JSON Locators**: Define all selectors in `shared/locators/*.json`
2. **Extend SelfHealingBasePage**: Use `smartClick()`, `smartType()` methods
3. **Keep Ollama Running**: Always have the LLM available for healing
4. **Review Healed Selectors**: Periodically review auto-updated locators
5. **Add Fallbacks**: More fallbacks = better resilience

---

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| `Healing service not available` | Start the server: `npm run server` |
| `Ollama connection refused` | Run `ollama serve` first |
| `Model not found` | Pull model: `ollama pull llama3.1:8b` |
| `Locator file not found` | Check path in `config.properties` |
| `Port 3001 in use` | Kill process: `lsof -i :3001` and `kill -9 PID` |

---

## 📄 License

MIT License - see [LICENSE](LICENSE) file

---

## 👨‍💻 Author

**Shreyans Saklecha**

- GitHub: [@Shreyans1999](https://github.com/Shreyans1999)

---

## 🙏 Acknowledgments

- [Playwright](https://playwright.dev/) - Modern browser automation
- [Selenium](https://selenium.dev/) - Industry standard web testing
- [Ollama](https://ollama.ai/) - Run LLMs locally
- [TestNG](https://testng.org/) - Next generation testing framework
