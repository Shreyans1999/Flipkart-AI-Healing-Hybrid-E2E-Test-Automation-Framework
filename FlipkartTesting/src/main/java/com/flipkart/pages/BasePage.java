package com.flipkart.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * BasePage - Abstract base class for all Page Objects.
 * Contains common functionality shared across all pages like:
 * - WebDriver and WebDriverWait initialization
 * - Common wait methods
 * - JavaScript execution utilities
 * - Popup handling
 * - Scroll utilities
 */
public abstract class BasePage {
    
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected JavascriptExecutor js;
    
    // Default timeout in seconds
    protected static final int DEFAULT_TIMEOUT = 15;
    protected static final int SHORT_TIMEOUT = 5;
    protected static final int LONG_TIMEOUT = 30;
    
    /**
     * Constructor - initializes WebDriver, WebDriverWait, and PageFactory
     * @param driver WebDriver instance
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }
    
    /**
     * Constructor with custom timeout
     * @param driver WebDriver instance
     * @param timeoutSeconds Custom timeout in seconds
     */
    public BasePage(WebDriver driver, int timeoutSeconds) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }
    
    // ==================== WAIT UTILITIES ====================
    
    /**
     * Wait for element to be clickable
     */
    protected WebElement waitForClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }
    
    /**
     * Wait for element to be clickable by locator
     */
    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
    
    /**
     * Wait for element to be visible
     */
    protected WebElement waitForVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }
    
    /**
     * Wait for element to be visible by locator
     */
    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    /**
     * Wait for element to be present in DOM
     */
    protected WebElement waitForPresence(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }
    
    /**
     * Wait for a specific duration (use sparingly, prefer explicit waits)
     */
    protected void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Wait for page to load completely
     */
    protected void waitForPageLoad() {
        wait.until(webDriver -> js.executeScript("return document.readyState").equals("complete"));
    }
    
    // ==================== CLICK UTILITIES ====================
    
    /**
     * Click element with JavaScript (useful when element is obscured)
     */
    protected void clickWithJS(WebElement element) {
        js.executeScript("arguments[0].click();", element);
    }
    
    /**
     * Safe click - tries native click first, falls back to JS click
     */
    protected void safeClick(WebElement element) {
        try {
            waitForClickable(element).click();
        } catch (Exception e) {
            clickWithJS(element);
        }
    }
    
    /**
     * Safe click with explicit wait and retry
     */
    protected void safeClick(By locator) {
        try {
            waitForClickable(locator).click();
        } catch (Exception e) {
            WebElement element = driver.findElement(locator);
            clickWithJS(element);
        }
    }
    
    // ==================== SCROLL UTILITIES ====================
    
    /**
     * Scroll element into view (center of screen)
     */
    protected void scrollIntoView(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        pause(300);
    }
    
    /**
     * Scroll by specific pixels
     */
    protected void scrollBy(int x, int y) {
        js.executeScript("window.scrollBy(" + x + ", " + y + ")");
        pause(300);
    }
    
    /**
     * Scroll to top of page
     */
    protected void scrollToTop() {
        js.executeScript("window.scrollTo(0, 0)");
        pause(300);
    }
    
    /**
     * Scroll to bottom of page
     */
    protected void scrollToBottom() {
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        pause(300);
    }
    
    // ==================== INPUT UTILITIES ====================
    
    /**
     * Clear field and enter text
     */
    protected void clearAndType(WebElement element, String text) {
        waitForClickable(element);
        element.clear();
        element.sendKeys(text);
    }
    
    /**
     * Set value using JavaScript (useful for readonly fields)
     */
    protected void setValueWithJS(WebElement element, String value) {
        js.executeScript("arguments[0].value = arguments[1];", element, value);
    }
    
    // ==================== POPUP/MODAL UTILITIES ====================
    
    /**
     * Close login popup if present (common on Flipkart)
     */
    protected void closeLoginPopupIfPresent() {
        try {
            String[] closeButtonXpaths = {
                "//button[contains(@class,'_2KpZ6l _2doB4z')]",
                "//button[@class='_2KpZ6l _2doB4z']",
                "//*[@class='_30XB9F']",
                "//span[text()='✕']/.."
            };
            
            for (String xpath : closeButtonXpaths) {
                try {
                    WebElement closeBtn = driver.findElement(By.xpath(xpath));
                    if (closeBtn.isDisplayed()) {
                        closeBtn.click();
                        System.out.println("Closed popup using: " + xpath);
                        pause(500);
                        return;
                    }
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            // No popup to close - this is fine
        }
    }
    
    /**
     * Click anywhere on body to close dropdowns/popups
     */
    protected void clickBodyToCloseDropdown() {
        js.executeScript("document.body.click();");
        pause(300);
    }
    
    // ==================== VERIFICATION UTILITIES ====================
    
    /**
     * Check if element is displayed
     */
    protected boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if element is displayed by locator
     */
    protected boolean isElementDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if element exists in DOM
     */
    protected boolean isElementPresent(By locator) {
        return driver.findElements(locator).size() > 0;
    }
    
    /**
     * Get current page URL
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
    
    /**
     * Get current page title
     */
    protected String getPageTitle() {
        return driver.getTitle();
    }
    
    // ==================== WINDOW UTILITIES ====================
    
    /**
     * Maximize browser window
     */
    protected void maximizeWindow() {
        driver.manage().window().maximize();
    }
    
    /**
     * Reset zoom level to 100%
     */
    protected void resetZoom() {
        js.executeScript("document.body.style.zoom='100%'");
    }
    
    /**
     * Switch to a new window/tab
     */
    protected void switchToNewWindow() {
        String originalWindow = driver.getWindowHandle();
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(originalWindow)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }
    }
    
    /**
     * Switch back to original/main window
     */
    protected void switchToMainWindow() {
        String mainWindow = driver.getWindowHandles().iterator().next();
        driver.switchTo().window(mainWindow);
    }
    
    // ==================== HIGHLIGHT UTILITY (for debugging) ====================
    
    /**
     * Highlight element for visual debugging
     */
    protected void highlightElement(WebElement element) {
        String originalStyle = element.getAttribute("style");
        js.executeScript("arguments[0].setAttribute('style', 'border: 3px solid red; background: yellow;');", element);
        pause(500);
        js.executeScript("arguments[0].setAttribute('style', '" + originalStyle + "');", element);
    }
}
