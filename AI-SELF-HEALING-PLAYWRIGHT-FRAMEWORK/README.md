# 🚀 AI-Powered Self-Healing Playwright Automation Framework

> **Enterprise-Grade End-to-End Test Automation using Playwright, TypeScript & AI (LLMs)**

[![Playwright Tests](https://github.com/your-repo/ai-self-healing-playwright/actions/workflows/playwright-ci.yml/badge.svg)](https://github.com/your-repo/ai-self-healing-playwright/actions)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.3-blue)](https://www.typescriptlang.org/)
[![Playwright](https://img.shields.io/badge/Playwright-1.40-green)](https://playwright.dev/)

---

## 📌 Overview

Modern web applications evolve rapidly. UI changes such as updated IDs, modified DOM structures, or redesigned components often cause **automation test failures**, leading to flaky tests, broken CI pipelines, and high maintenance overhead.

This project introduces an **AI-powered Self-Healing Test Automation Framework** built using **Playwright + TypeScript**, designed to **automatically detect, heal, and recover from locator failures at runtime** using **LLM-based reasoning**.

---

## 🎯 Key Features

- ✅ **Self-Healing Locators** - Automatically recover from broken selectors
- ✅ **AI-Powered** - Uses OpenAI/Azure/Ollama for intelligent selector generation
- ✅ **Fallback Strategy** - Multiple fallback selectors per element
- ✅ **Auto-Update** - Automatically updates locator files after healing
- ✅ **Structured Logging** - Winston-based JSON logging with healing analytics
- ✅ **Allure Reporting** - Rich test reports with healing annotations
- ✅ **CI/CD Ready** - GitHub Actions pipeline with parallel execution
- ✅ **Page Object Model** - Clean, maintainable test architecture

---

## 🏗️ Architecture

```
Test Layer (E2E Specs)
       ↓
Page Object Layer (UI Actions)
       ↓
Smart Locator Resolver
       ↓
AI Self-Healing Engine
       ↓
Utilities / Config / Logging / Reporting
```

---

## 📁 Project Structure

```
ai-self-healing-playwright/
├── src/
│   ├── tests/e2e/           # E2E test specifications
│   │   ├── login.spec.ts
│   │   ├── checkout.spec.ts
│   │   └── user-flow.spec.ts
│   │
│   ├── pages/               # Page Object classes
│   │   ├── BasePage.ts      # Base with smart actions
│   │   ├── LoginPage.ts
│   │   └── DashboardPage.ts
│   │
│   ├── locators/            # JSON locator files
│   │   ├── login.locators.json
│   │   └── dashboard.locators.json
│   │
│   ├── ai/                  # Self-Healing Engine
│   │   ├── HealingEngine.ts # Main orchestrator
│   │   ├── LLMClient.ts     # OpenAI/Azure/Ollama
│   │   ├── LocatorAnalyzer.ts
│   │   └── HealingStrategy.ts
│   │
│   ├── core/                # Framework core
│   │   ├── TestSetup.ts
│   │   ├── RetryHandler.ts
│   │   └── PlaywrightHooks.ts
│   │
│   ├── utils/               # Utilities
│   │   ├── Logger.ts
│   │   ├── DomSnapshot.ts
│   │   ├── FileUtils.ts
│   │   └── EnvLoader.ts
│   │
│   └── config/              # Configuration
│       ├── playwright.config.ts
│       ├── env.dev.ts
│       ├── env.qa.ts
│       └── env.prod.ts
│
├── reports/                 # Test reports
├── logs/                    # Log files
├── .github/workflows/       # CI/CD
├── playwright.config.ts     # Root config
├── package.json
├── tsconfig.json
└── .env
```

---

## 🚀 Quick Start

### Prerequisites

- Node.js 18+
- npm or yarn
- OpenAI API key (or Azure OpenAI/Ollama for local)

### Installation

```bash
# Clone the repository
git clone https://github.com/your-repo/ai-self-healing-playwright.git
cd ai-self-healing-playwright

# Install dependencies
npm install

# Install Playwright browsers
npx playwright install
```

### Configuration

1. Copy `.env` and add your API key:
```bash
# .env
OPENAI_API_KEY=your-api-key-here
LLM_PROVIDER=openai
```

2. Run tests:
```bash
# Run all tests
npm test

# Run with UI
npm run test:ui

# Run headed
npm run test:headed

# Run specific browser
npm run test:chromium
```

---

## 🔧 Self-Healing Flow

When a locator fails, the framework:

1. **Detects Failure** - Catches Playwright exceptions
2. **Tries Fallbacks** - Attempts fallback selectors from JSON
3. **Captures DOM** - Snapshots page structure
4. **Queries LLM** - Asks AI for alternative selectors
5. **Validates** - Tests proposed selectors in browser
6. **Updates File** - Saves working selector to JSON
7. **Retries** - Re-executes the failed action
8. **Logs Event** - Records healing for analytics

```
Test Step
 → Locator Lookup
 → Action Attempt
 → Failure Detected ❌
 → AI Healing Engine 🔧
 → Locator Validation ✓
 → Retry Action
 → Continue Test ✅
```

---

## 📝 Locator Format

Locators are stored in JSON with primary and fallback selectors:

```json
{
  "loginButton": {
    "primary": "#login-btn",
    "fallbacks": [
      "button:has-text('Login')",
      "[data-testid='login-button']",
      "button[type='submit']"
    ]
  }
}
```

---

## 🧪 Writing Tests

Tests use Page Objects with clean APIs:

```typescript
test('User login flow', async ({ page }, testInfo) => {
  const loginPage = new LoginPage(page, testInfo);
  const dashboardPage = new DashboardPage(page, testInfo);

  // Clean, readable test - no locators!
  await loginPage.navigateToLogin();
  await loginPage.login('tomsmith', 'SuperSecretPassword!');
  
  await dashboardPage.waitForDashboard();
  expect(await dashboardPage.getWelcomeMessage()).toContain('secure area');
});
```

---

## ⚙️ Environment Configuration

| Variable | Description | Default |
|----------|-------------|---------|
| `LLM_PROVIDER` | AI provider (openai/azure/ollama) | `openai` |
| `OPENAI_API_KEY` | OpenAI API key | - |
| `TEST_ENV` | Environment (dev/qa/prod) | `dev` |
| `BASE_URL` | Application base URL | `https://the-internet.herokuapp.com` |
| `MAX_HEALING_RETRIES` | Max healing attempts | `3` |
| `HEALING_CONFIDENCE_THRESHOLD` | Min confidence for healing | `0.7` |
| `AUTO_UPDATE_LOCATORS` | Auto-update locator files | `true` |

---

## 📊 Reporting

### Allure Report
```bash
npm run allure:generate
npm run allure:open
```

### Healing Logs
Check `logs/healing.log` for detailed healing events:
```json
{
  "level": "info",
  "message": "Healing succeeded for loginButton",
  "originalSelector": "#broken-selector",
  "healedSelector": "button:has-text('Login')",
  "confidenceScore": 0.85,
  "duration": 1234
}
```

---

## 🚦 CI/CD

GitHub Actions workflow runs:
- On push to main/master
- On pull requests
- Tests all browsers in parallel
- Uploads Allure reports
- Captures healing analytics

---

## 🔌 LLM Providers

### OpenAI (Default)
```env
LLM_PROVIDER=openai
OPENAI_API_KEY=sk-xxx
OPENAI_MODEL=gpt-4-turbo-preview
```

### Ollama (Local - Free) ⭐ Recommended

Ollama allows you to run AI models locally for free. Here's the complete setup guide:

#### Step 1: Install Ollama

**macOS (Homebrew):**
```bash
brew install ollama
```

**macOS/Linux (Official):**
```bash
curl -fsSL https://ollama.ai/install.sh | sh
```

**Windows:**
Download from [ollama.ai/download](https://ollama.ai/download)

#### Step 2: Start Ollama Server

```bash
# Start the server (keep this terminal open)
ollama serve
```

The server runs at `http://localhost:11434` by default.

#### Step 3: Pull the Model

```bash
# Recommended model for selector healing (~4.9GB download)
ollama pull llama3.1:8b
```

**Alternative Models:**
| Model | Size | Speed | Quality |
|-------|------|-------|---------|
| `llama3.1:8b` | 4.9GB | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| `mistral:7b` | 4GB | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| `codellama:13b` | 7GB | ⭐⭐ | ⭐⭐⭐⭐⭐ |
| `qwen2.5:7b` | 4.4GB | ⭐⭐⭐ | ⭐⭐⭐⭐ |

#### Step 4: Configure .env

```env
LLM_PROVIDER=ollama
OLLAMA_BASE_URL=http://localhost:11434
OLLAMA_MODEL=llama3.1:8b
```

#### Step 5: Verify Setup

```bash
# Test the model
ollama run llama3.1:8b "Hello, can you generate a CSS selector?"

# Check running models
ollama list
```

#### Troubleshooting

| Issue | Solution |
|-------|----------|
| "ollama server not responding" | Run `ollama serve` in a separate terminal |
| "model not found" | Run `ollama pull llama3.1:8b` |
| Slow responses | Try `mistral:7b` for faster inference |
| Out of memory | Use a smaller model like `llama3.2:3b` |

---

## 🏆 Best Practices

- ✅ Clean architecture with SOLID principles
- ✅ Zero hard-coded selectors
- ✅ Environment isolation
- ✅ AI used only when failures occur
- ✅ Interview-ready, maintainable codebase

---

## 📬 Future Enhancements

- [ ] Visual AI comparison
- [ ] Historical flaky test analytics
- [ ] Confidence-based healing approval
- [ ] Dashboard for healing metrics
- [ ] Slack/Teams notifications

---

## 👨‍💻 Author

**SDET / QA Automation Engineer**  
Focused on scalable test architecture, CI/CD stability, and AI-driven automation solutions.

---

## 📄 License

MIT License
