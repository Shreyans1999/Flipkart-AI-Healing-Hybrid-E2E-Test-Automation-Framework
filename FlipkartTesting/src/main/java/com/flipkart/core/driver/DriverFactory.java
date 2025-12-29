package com.flipkart.core.driver;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * DriverFactory - Thread-safe WebDriver management using ThreadLocal.
 * Implements Singleton pattern for driver lifecycle management.
 * Supports Chrome, Firefox, Edge, and Headless modes.
 */
public class DriverFactory {
    
    // ThreadLocal ensures each thread gets its own WebDriver instance
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    
    // Supported browser types
    public enum BrowserType {
        CHROME, FIREFOX, EDGE, CHROME_HEADLESS, FIREFOX_HEADLESS
    }
    
    // Private constructor to prevent direct instantiation
    private DriverFactory() {}
    
    /**
     * Initialize and return WebDriver for the specified browser type
     * @param browserType Browser to initialize
     * @return WebDriver instance
     */
    public static WebDriver initDriver(BrowserType browserType) {
        WebDriver driver = null;
        
        switch (browserType) {
            case CHROME:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--start-maximized");
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--disable-popup-blocking");
                driver = new ChromeDriver(chromeOptions);
                break;
                
            case CHROME_HEADLESS:
                WebDriverManager.chromedriver().setup();
                ChromeOptions headlessChromeOptions = new ChromeOptions();
                headlessChromeOptions.addArguments("--headless=new");
                headlessChromeOptions.addArguments("--window-size=1920,1080");
                headlessChromeOptions.addArguments("--disable-gpu");
                headlessChromeOptions.addArguments("--no-sandbox");
                driver = new ChromeDriver(headlessChromeOptions);
                break;
                
            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                driver = new FirefoxDriver(firefoxOptions);
                driver.manage().window().maximize();
                break;
                
            case FIREFOX_HEADLESS:
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions headlessFirefoxOptions = new FirefoxOptions();
                headlessFirefoxOptions.addArguments("--headless");
                headlessFirefoxOptions.addArguments("--window-size=1920,1080");
                driver = new FirefoxDriver(headlessFirefoxOptions);
                break;
                
            case EDGE:
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                driver = new EdgeDriver(edgeOptions);
                driver.manage().window().maximize();
                break;
                
            default:
                throw new IllegalArgumentException("Unsupported browser type: " + browserType);
        }
        
        // Set implicit wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        
        // Store in ThreadLocal
        driverThreadLocal.set(driver);
        
        return driver;
    }
    
    /**
     * Initialize driver from browser name string (for TestNG parameters)
     * @param browserName Browser name as string
     * @return WebDriver instance
     */
    public static WebDriver initDriver(String browserName) {
        BrowserType browserType;
        
        switch (browserName.toLowerCase().trim()) {
            case "chrome":
                browserType = BrowserType.CHROME;
                break;
            case "firefox":
                browserType = BrowserType.FIREFOX;
                break;
            case "edge":
                browserType = BrowserType.EDGE;
                break;
            case "headless":
            case "chrome-headless":
            case "chrome_headless":
                browserType = BrowserType.CHROME_HEADLESS;
                break;
            case "firefox-headless":
            case "firefox_headless":
                browserType = BrowserType.FIREFOX_HEADLESS;
                break;
            default:
                System.out.println("Unknown browser '" + browserName + "', defaulting to Chrome");
                browserType = BrowserType.CHROME;
        }
        
        return initDriver(browserType);
    }
    
    /**
     * Get the current thread's WebDriver instance
     * @return WebDriver for current thread
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver not initialized. Call initDriver() first.");
        }
        return driver;
    }
    
    /**
     * Check if driver is initialized for current thread
     * @return true if driver exists
     */
    public static boolean hasDriver() {
        return driverThreadLocal.get() != null;
    }
    
    /**
     * Quit and remove the current thread's WebDriver
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Error quitting driver: " + e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }
    
    /**
     * Navigate to a URL
     * @param url URL to navigate to
     */
    public static void navigateTo(String url) {
        getDriver().get(url);
    }
    
    /**
     * Get current page title
     * @return Page title
     */
    public static String getPageTitle() {
        return getDriver().getTitle();
    }
    
    /**
     * Get current URL
     * @return Current URL
     */
    public static String getCurrentUrl() {
        return getDriver().getCurrentUrl();
    }
}
