# AI Self-Healing Test Scenario: Flipkart Search Button

This document demonstrates how to intentionally break a locator and observe the AI self-healing process in action.

---

## Scenario Overview

We will:
1. Set a **broken selector** for the search button
2. Run a test that tries to click it
3. Watch the healing engine recover automatically

---

## Step 1: Break the Search Button Locator

Edit `shared/locators/flipkart-homepage.locators.json`:

```json
{
  "searchButton": {
    "primary": "#completely-broken-search-selector",
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

Create `FlipkartSelfHealingDemo.java` in `src/main/java/com/flipkart/pages/`:

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
    
    public void searchForProduct(String query) {
        // Type in search box
        smartType("searchBox", query);
        
        // This will fail with broken selector, then trigger AI healing
        smartClick("searchButton");
    }
}
```

---

## Step 3: Create Demo Test

Create `SelfHealingDemoTest.java` in `src/test/java/com/flipkart/tests/e2e/`:

```java
package com.flipkart.tests.e2e;

import org.testng.annotations.Test;
import com.flipkart.pages.FlipkartSelfHealingDemo;
import com.flipkart.tests.base.BaseTest;

public class SelfHealingDemoTest extends BaseTest {
    
    @Test(description = "Demo: Watch AI heal a broken search button selector")
    public void testSearchButtonHealing() {
        // Navigate to Flipkart
        navigateToBaseUrl();
        
        // Create self-healing page
        FlipkartSelfHealingDemo page = new FlipkartSelfHealingDemo(driver);
        
        // Close popup if present
        page.closeLoginPopupIfPresent();
        
        // This will:
        // 1. Try "#completely-broken-search-selector" -> FAIL
        // 2. Try fallbacks -> ALL FAIL
        // 3. Capture DOM snapshot
        // 4. Call AI healing API
        // 5. LLM generates: "button[type='submit']" or similar
        // 6. Validate and use healed selector
        // 7. Click succeeds!
        page.searchForProduct("iPhone 15");
        
        logger.info("✅ Search executed with healed selector!");
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

Or in Eclipse: Right-click `SelfHealingDemoTest.java` → Run As → TestNG Test

---

## Expected Console Output

Watch for these healing logs:

```
🔧 Requesting AI healing for: searchButton
   failedSelector: #completely-broken-search-selector

Trying 3 fallback selectors
   #also-broken-1 -> FAILED
   #also-broken-2 -> FAILED
   #also-broken-3 -> FAILED

Fallbacks failed, querying LLM...

LLM returned 4 selectors:
   1. button[type='submit']
   2. //button[@type='submit']
   3. button:has-text('Search')
   4. //button[@aria-label='Search']

✅ LLM selector validated: button[type='submit']
   Confidence: 0.85

Locator file updated: flipkart-homepage.searchButton -> button[type='submit']

✅ Healing successful!
```

---

## Step 5: Verify Locator Was Updated

After the test, check `shared/locators/flipkart-homepage.locators.json`:

```json
{
  "searchButton": {
    "primary": "button[type='submit']",
    "fallbacks": [
      "#completely-broken-search-selector",
      "#also-broken-1",
      "#also-broken-2"
    ],
    "lastHealed": "2025-12-29T20:46:00.000Z",
    "healCount": 1
  }
}
```

The healed selector is now the primary!

---

## LLM Configuration

Configure in `AI-SELF-HEALING-PLAYWRIGHT-FRAMEWORK/.env`:

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

## Quick Commands

```bash
# Start healing server
cd AI-SELF-HEALING-PLAYWRIGHT-FRAMEWORK && npm run server

# Run demo test
cd FlipkartTesting && mvn test -Dtest=SelfHealingDemoTest
```
