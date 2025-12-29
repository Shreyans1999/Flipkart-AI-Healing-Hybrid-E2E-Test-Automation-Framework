# AI Self-Healing Test Scenario: Flipkart Login Button

This document demonstrates how to intentionally break a locator and observe the AI self-healing process in action.

---

## Scenario Overview

We will:
1. Set a **broken selector** for the login button
2. Run a test that tries to click it
3. Watch the healing engine recover automatically

---

## Step 1: Break the Login Button Locator

Edit `shared/locators/flipkart-homepage.locators.json`:

```json
{
  "loginButton": {
    "primary": "#completely-broken-selector-that-will-fail",
    "fallbacks": [
      "#also-broken-1",
      "#also-broken-2",
      "#also-broken-3"
    ]
  }
}
```

The primary and all fallbacks are intentionally broken to trigger AI healing.

---

## Step 2: Create Test Page Object

Create `FlipkartSelfHealingDemo.java`:

```java
package com.flipkart.pages;

import org.openqa.selenium.WebDriver;

public class FlipkartSelfHealingDemo extends SelfHealingBasePage {
    
    @Override
    protected String getPageName() {
        return "flipkart-homepage";
    }
    
    public FlipkartSelfHealingDemo(WebDriver driver) {
        super(driver);
    }
    
    public void clickLoginButton() {
        // This will fail with broken selector
        // Then trigger AI healing
        smartClick("loginButton");
    }
}
```

---

## Step 3: Create Demo Test

Create `SelfHealingDemoTest.java`:

```java
package com.flipkart.tests.e2e;

import org.testng.annotations.Test;
import com.flipkart.pages.FlipkartSelfHealingDemo;
import com.flipkart.tests.base.BaseTest;

public class SelfHealingDemoTest extends BaseTest {
    
    @Test(description = "Demo: Watch AI heal a broken login button selector")
    public void testLoginButtonHealing() {
        // Navigate to Flipkart
        navigateToBaseUrl();
        
        // Create self-healing page
        FlipkartSelfHealingDemo page = new FlipkartSelfHealingDemo(driver);
        
        // Close popup if present
        page.closeLoginPopupIfPresent();
        
        // This will:
        // 1. Try "#completely-broken-selector-that-will-fail" -> FAIL
        // 2. Try fallbacks -> ALL FAIL
        // 3. Capture DOM snapshot
        // 4. Call AI healing API
        // 5. LLM generates: "a:has-text('Login')" or similar
        // 6. Validate and use healed selector
        // 7. Click succeeds!
        page.clickLoginButton();
        
        logger.info("✅ Login button clicked successfully with healed selector!");
    }
}
```

---

## Step 4: Run the Demo

### Terminal 1: Start Healing Server

```bash
cd AI-SELF-HEALING-PLAYWRIGHT-FRAMEWORK
npm run server
```

You should see:
```
🚀 Healing API Server running on http://localhost:3001
```

### Terminal 2: Run the Test

```bash
cd FlipkartTesting
mvn test -Dtest=SelfHealingDemoTest
```

---

## Expected Console Output

Watch for these healing logs:

```
🔧 Requesting AI healing for: loginButton
   failedSelector: #completely-broken-selector-that-will-fail

Trying 3 fallback selectors
   #also-broken-1 -> FAILED
   #also-broken-2 -> FAILED
   #also-broken-3 -> FAILED

Fallbacks failed, querying LLM...

LLM returned 4 selectors:
   1. a:has-text('Login')
   2. //a[contains(@href,'login')]
   3. span:has-text('Login')
   4. [data-testid='login-link']

✅ LLM selector validated: //a[contains(@href,'login')]
   Confidence: 0.85

Locator file updated: flipkart-homepage.loginButton -> //a[contains(@href,'login')]

✅ Healing successful!
```

---

## Step 5: Verify Locator Was Updated

After the test, check `shared/locators/flipkart-homepage.locators.json`:

```json
{
  "loginButton": {
    "primary": "//a[contains(@href,'login')]",
    "fallbacks": [
      "#completely-broken-selector-that-will-fail",
      "#also-broken-1",
      "#also-broken-2"
    ],
    "lastHealed": "2025-12-29T19:50:00.000Z",
    "healCount": 1
  }
}
```

The healed selector is now the primary, and the old broken one moved to fallbacks!

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| "Healing service not available" | Run `npm run server` first |
| "LLM returned no selectors" | Check your API key in `.env` |
| "No valid selector found" | The LLM couldn't find the element |

---

## LLM Configuration

For this demo to work, configure one of these in `.env`:

**Option A: Ollama (Free, Local)**
```env
LLM_PROVIDER=ollama
OLLAMA_BASE_URL=http://localhost:11434
OLLAMA_MODEL=llama3.1:8b
```

**Option B: OpenAI (Paid)**
```env
LLM_PROVIDER=openai
OPENAI_API_KEY=sk-your-key-here
```

---

## Quick Test Commands

```bash
# Start everything
cd AI-SELF-HEALING-PLAYWRIGHT-FRAMEWORK && npm run server &
cd FlipkartTesting && mvn test -Dtest=SelfHealingDemoTest

# Check healing logs
tail -f AI-SELF-HEALING-PLAYWRIGHT-FRAMEWORK/logs/healing.log
```
