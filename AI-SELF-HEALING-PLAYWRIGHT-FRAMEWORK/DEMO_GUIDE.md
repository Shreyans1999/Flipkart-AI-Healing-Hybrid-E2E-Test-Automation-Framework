# 🎯 Self-Healing Demo Guide for Interviews

This guide helps you demonstrate the AI Self-Healing feature to interviewers in a clear, impressive way.

---

## 📋 Pre-Demo Checklist

- [ ] Ollama is running (`ollama serve` in a terminal)
- [ ] Model pulled (`ollama pull llama3.1:8b`)
- [ ] `.env` configured with `LLM_PROVIDER=ollama`
- [ ] Internet connection (for test website)
- [ ] Run `npm install` to ensure dependencies are installed
- [ ] Run tests once to confirm they pass before demo

---

## 🎬 Demo Script (5-7 minutes)

### Step 1: Explain the Problem (30 seconds)

> *"In large teams, UI changes constantly break automated tests. QA engineers spend 30-50% of their time just fixing broken locators. This framework solves that with AI."*

### Step 2: Show Working Test (1 minute)

Run a passing test:
```bash
npx playwright test login.spec.ts --headed --project=chromium
```

> *"Here's our login test working normally with correct selectors."*

### Step 3: Break the Locator (1 minute)

Open `src/locators/login.locators.json` and make this change:

#### 🔴 BREAK THIS (Login Button):

**BEFORE (working):**
```json
"loginButton": {
    "primary": "button[type='submit']",
    "fallbacks": [
        "button.radius",
        "#login button",
        "button:has-text('Login')",
        "form#login button"
    ]
}
```

**AFTER (broken - copy this):**
```json
"loginButton": {
    "primary": "#completely-wrong-selector",
    "fallbacks": [
        "#also-broken",
        "#this-wont-work-either"
    ]
}
```

> *"I've intentionally broken ALL selectors for the login button. In traditional frameworks, this test would fail."*

### Step 4: Run the Test Again (2-3 minutes)

```bash
npx playwright test -g "Valid login" --headed --project=chromium
```

**What happens:**
1. ❌ Primary selector fails  
2. ❌ All fallback selectors fail  
3. 🧠 **AI Healing Engine activates**
4. 📸 DOM snapshot captured
5. 🤖 LLM called with page context
6. ✅ New selector validated
7. 🎉 **Test PASSES!**
8. 📝 Locator file auto-updated

> *"Watch the console - you'll see the healing process in real-time. The AI analyzed the DOM and found the correct selector."*

### Step 5: Show the Healed File (30 seconds)

Open `src/locators/login.locators.json` again:

> *"Look - the framework automatically updated the selector! Future test runs will use this healed selector without calling AI again."*

### Step 6: Show Logs (Optional - 30 seconds)

```bash
cat logs/healing_events.log | tail -20
```

> *"Every healing event is logged for auditing - this is important for CI/CD pipelines."*

---

## 🛠️ Elements You Can Break for Demo

### Option A: Login Button (Recommended - Most Visual)

**File:** `src/locators/login.locators.json`

```json
"loginButton": {
    "primary": "#broken-button",
    "fallbacks": ["#wrong1", "#wrong2"]
}
```

**Test to run:** `npx playwright test -g "Valid login" --headed --project=chromium`

---

### Option B: Username Input

**File:** `src/locators/login.locators.json`

```json
"usernameInput": {
    "primary": "#wrong-username-field",
    "fallbacks": ["#broken1", "#broken2"]
}
```

**Test to run:** `npx playwright test -g "Valid login" --headed --project=chromium`

---

### Option C: Password Input

**File:** `src/locators/login.locators.json`

```json
"passwordInput": {
    "primary": "#wrong-password-field",
    "fallbacks": ["#broken1", "#broken2"]
}
```

**Test to run:** `npx playwright test -g "Valid login" --headed --project=chromium`

---

### Option D: Dashboard Logout Button (After Login)

**File:** `src/locators/dashboard.locators.json`

```json
"logoutButton": {
    "primary": "#wrong-logout",
    "fallbacks": ["#broken1", "#broken2"]
}
```

**Test to run:** `npx playwright test -g "Logout" --headed --project=chromium`

---

## 🎤 Key Talking Points

| What to Say | Why It Impresses |
|-------------|------------------|
| *"The AI uses semantic understanding of the page"* | Shows technical depth |
| *"Healed selectors are persisted - no repeated API calls"* | Shows efficiency |
| *"This integrates into CI/CD - builds don't get blocked"* | Shows business value |
| *"Supports OpenAI, Azure, and local Ollama"* | Shows flexibility |

---

## 🔧 Restore Original Selectors

After the demo, restore the working selectors:

### Login Button (Original):
```json
"loginButton": {
    "primary": "button[type='submit']",
    "fallbacks": [
        "button.radius",
        "#login button",
        "button:has-text('Login')",
        "form#login button"
    ]
}
```

### Username Input (Original):
```json
"usernameInput": {
    "primary": "#username",
    "fallbacks": [
        "input[name='username']",
        "[data-testid='username']",
        "input[placeholder*='username' i]",
        "//input[@id='username']"
    ]
}
```

### Password Input (Original):
```json
"passwordInput": {
    "primary": "#password",
    "fallbacks": [
        "input[name='password']",
        "[data-testid='password']",
        "input[type='password']",
        "//input[@id='password']"
    ]
}
```

---

## ⚠️ Troubleshooting

| Issue | Solution |
|-------|----------|
| Test fails even with healing | Check Ollama is running (`ollama serve`) |
| LLM not responding | Run `ollama list` to verify model is available |
| No healing logs | Ensure `logs/` directory exists |
| Test passes immediately | Make sure you broke ALL fallbacks too |
| Model pulls wrong selectors | Ensure `.env` has `OLLAMA_MODEL=llama3.1:8b` |

---

## 📊 Architecture to Explain

```
Test Runs → Locator Fails → Fallbacks Fail → AI Healing Engine
                                                    ↓
                              ┌─────────────────────┴───────────────────────┐
                              ↓                                             ↓
                    Capture DOM Snapshot                           Send to LLM
                              ↓                                             ↓
                    Validate in Browser  ←─────────────────  Get Alternative Selectors
                              ↓
                    Update Locator JSON
                              ↓
                    Test Continues ✅
```

---

**Good luck with your interview! 🚀**
