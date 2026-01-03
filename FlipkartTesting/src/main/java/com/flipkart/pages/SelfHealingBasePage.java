package com.flipkart.pages;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.flipkart.core.healing.HealingConfig;
import com.flipkart.core.healing.HealingRequest;
import com.flipkart.core.healing.HealingResult;
import com.flipkart.core.healing.HealingServiceClient;
import com.flipkart.core.healing.LocatorConfig;
import com.flipkart.core.healing.LocatorConfig.LocatorEntry;

/**
 * SelfHealingBasePage - Base class with AI-powered self-healing locator
 * capabilities
 * 
 * Extends BasePage with intelligent element finding that:
 * 1. Uses JSON-based locators with primary + fallback selectors
 * 2. On failure, captures DOM and requests AI healing
 * 3. Updates locator files with healed selectors
 * 
 * Page objects can extend this class and use smart* methods for
 * automatic self-healing capabilities.
 */
public abstract class SelfHealingBasePage extends BasePage {

    protected static final Logger logger = LogManager.getLogger(SelfHealingBasePage.class);

    private final HealingServiceClient healingClient;
    private final LocatorConfig locatorConfig;
    private final HealingConfig healingConfig;

    /**
     * Get the page name used for locator file lookup
     * e.g., "flipkart-homepage" maps to flipkart-homepage.locators.json
     */
    protected abstract String getPageName();

    public SelfHealingBasePage(WebDriver driver) {
        super(driver);
        this.healingClient = HealingServiceClient.getInstance();
        this.locatorConfig = LocatorConfig.getInstance();
        this.healingConfig = HealingConfig.getInstance();
    }

    public SelfHealingBasePage(WebDriver driver, int timeoutSeconds) {
        super(driver, timeoutSeconds);
        this.healingClient = HealingServiceClient.getInstance();
        this.locatorConfig = LocatorConfig.getInstance();
        this.healingConfig = HealingConfig.getInstance();
        logger.info("SelfHealingBasePage initialized - healing enabled: " + healingConfig.isEnabled());
        logger.info("Healing service URL: " + healingConfig.getServiceUrl());
    }

    // ==================== SMART ELEMENT METHODS ====================

    /**
     * Smart click with self-healing
     */
    protected void smartClick(String elementKey) {
        WebElement element = findWithHealing(elementKey, "click");
        if (element != null) {
            safeClick(element);
            logger.debug("Smart clicked: " + elementKey);
        }
    }

    /**
     * Smart type with self-healing
     */
    protected void smartType(String elementKey, String text) {
        WebElement element = findWithHealing(elementKey, "fill");
        if (element != null) {
            clearAndType(element, text);
            logger.debug("Smart typed into: " + elementKey);
        }
    }

    /**
     * Smart wait for visible with self-healing
     */
    protected WebElement smartWaitForVisible(String elementKey) {
        WebElement element = findWithHealing(elementKey, "visible");
        if (element != null) {
            waitForVisible(element);
        }
        return element;
    }

    /**
     * Smart get text with self-healing
     */
    protected String smartGetText(String elementKey) {
        WebElement element = findWithHealing(elementKey, "text");
        return element != null ? element.getText() : "";
    }

    /**
     * Smart check if element is visible
     */
    protected boolean smartIsVisible(String elementKey) {
        try {
            WebElement element = findWithHealing(elementKey, "visible");
            return element != null && element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Smart get attribute with self-healing
     */
    protected String smartGetAttribute(String elementKey, String attributeName) {
        WebElement element = findWithHealing(elementKey, "any");
        return element != null ? element.getAttribute(attributeName) : "";
    }

    /**
     * Get element with self-healing (for custom operations)
     */
    protected WebElement smartFind(String elementKey) {
        return findWithHealing(elementKey, "any");
    }

    // ==================== CORE HEALING LOGIC ====================

    /**
     * Find element with self-healing capabilities
     */
    private WebElement findWithHealing(String elementKey, String action) {
        LocatorEntry locatorEntry = locatorConfig.getLocator(getPageName(), elementKey);

        // If no locator config, fall back to element key as XPath
        if (locatorEntry == null) {
            logger.warn("No locator config found for: " + getPageName() + "." + elementKey);
            try {
                return driver.findElement(By.xpath(elementKey));
            } catch (Exception e) {
                return null;
            }
        }

        String primarySelector = locatorEntry.getPrimary();

        // Try primary selector first
        WebElement element = tryFindElement(primarySelector);
        if (element != null) {
            return element;
        }

        logger.warn("Primary selector failed for " + elementKey + ": " + primarySelector);

        // Try fallback selectors
        List<String> fallbacks = locatorEntry.getFallbacks();
        if (fallbacks != null && !fallbacks.isEmpty()) {
            for (String fallback : fallbacks) {
                element = tryFindElement(fallback);
                if (element != null) {
                    logger.info("✅ Fallback selector worked: " + fallback);
                    // Update primary selector to the working fallback
                    locatorConfig.updateLocator(getPageName(), elementKey, fallback);
                    return element;
                }
            }
        }

        // All selectors failed - request AI healing if enabled
        logger.info("All selectors failed. Checking healing availability...");
        logger.info("Healing enabled: " + healingConfig.isEnabled());
        logger.info("Healing client available: " + healingClient.isHealingAvailable());

        if (healingConfig.isEnabled() && healingClient.isHealingAvailable()) {
            logger.info("🔧 Requesting AI healing for: " + elementKey);

            HealingResult result = requestHealing(elementKey, primarySelector, fallbacks, action);
            logger.info("Healing result: success=" + result.isSuccess() + ", selector=" + result.getHealedSelector()
                    + ", error=" + result.getError());

            if (result.isSuccess() && result.getHealedSelector() != null) {
                element = tryFindElement(result.getHealedSelector());
                if (element != null) {
                    logger.info("✅ AI healing successful: " + result.getHealedSelector());
                    locatorConfig.updateLocator(getPageName(), elementKey, result.getHealedSelector());
                    return element;
                }
            } else {
                logger.error("❌ AI healing failed: " + result.getError());
            }
        } else {
            logger.error("❌ Healing not available - enabled: " + healingConfig.isEnabled() + ", available: "
                    + healingClient.isHealingAvailable());
        }

        throw new NoSuchElementException("Could not find element: " + getPageName() + "." + elementKey +
                " (primary: " + primarySelector + ")");
    }

    /**
     * Try to find element with a selector (handles both XPath and CSS)
     */
    private WebElement tryFindElement(String selector) {
        try {
            if (selector == null || selector.isEmpty()) {
                return null;
            }

            By locator;
            if (selector.startsWith("//") || selector.startsWith("(//")) {
                locator = By.xpath(selector);
            } else if (selector.contains(":has-text(") || selector.startsWith("text=")) {
                // Playwright-style selectors not supported in Selenium
                // Try to extract text and use XPath
                String text = extractTextFromSelector(selector);
                locator = By.xpath("//*[contains(text(), '" + text + "')]");
            } else {
                locator = By.cssSelector(selector);
            }

            return wait.until(driver -> {
                try {
                    WebElement el = driver.findElement(locator);
                    return el.isDisplayed() ? el : null;
                } catch (Exception e) {
                    return null;
                }
            });

        } catch (TimeoutException | NoSuchElementException e) {
            return null;
        } catch (Exception e) {
            logger.debug("Selector parsing error for: " + selector + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Extract text from Playwright-style selectors
     */
    private String extractTextFromSelector(String selector) {
        if (selector.startsWith("text=")) {
            return selector.substring(5);
        }
        if (selector.contains(":has-text(")) {
            int start = selector.indexOf(":has-text(") + 10;
            int end = selector.indexOf(")", start);
            if (end > start) {
                String text = selector.substring(start, end);
                return text.replaceAll("^['\"]|['\"]$", ""); // Remove quotes
            }
        }
        return selector;
    }

    /**
     * Request healing from the AI service
     */
    private HealingResult requestHealing(String elementKey, String failedSelector,
            List<String> fallbacks, String action) {
        // Capture current page DOM
        String domSnapshot = captureDOM();

        HealingRequest request = HealingRequest.builder()
                .pageName(getPageName())
                .elementKey(elementKey)
                .failedSelector(failedSelector)
                .domSnapshot(domSnapshot)
                .action(action)
                .fallbacks(fallbacks)
                .build();

        return healingClient.heal(request);
    }

    /**
     * Capture the current page DOM for healing context
     */
    private String captureDOM() {
        try {
            return (String) js.executeScript("return document.documentElement.outerHTML;");
        } catch (Exception e) {
            logger.warn("Failed to capture DOM: " + e.getMessage());
            return "<html><body>DOM capture failed</body></html>";
        }
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Get selector for an element key from config
     */
    protected String getSelector(String elementKey) {
        return locatorConfig.getPrimarySelector(getPageName(), elementKey);
    }

    /**
     * Convert element key to By locator
     */
    protected By getLocator(String elementKey) {
        String selector = getSelector(elementKey);
        if (selector == null) {
            return By.xpath(elementKey); // Treat as XPath if no config
        }

        if (selector.startsWith("//") || selector.startsWith("(//")) {
            return By.xpath(selector);
        }
        return By.cssSelector(selector);
    }
}
