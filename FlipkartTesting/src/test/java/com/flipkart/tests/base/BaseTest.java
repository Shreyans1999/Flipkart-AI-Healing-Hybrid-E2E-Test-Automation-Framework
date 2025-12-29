package com.flipkart.tests.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.flipkart.core.config.ConfigManager;
import com.flipkart.core.driver.DriverFactory;
import com.flipkart.pages.FlipkartLoginPage;

/**
 * BaseTest - Abstract base class for all test classes.
 * Uses DriverFactory for thread-safe driver management.
 * Uses ConfigManager for configuration.
 * TestListener handles reporting automatically.
 */
public abstract class BaseTest {
    
    protected static final Logger logger = LogManager.getLogger(BaseTest.class);
    protected ConfigManager config;
    protected WebDriver driver;
    protected boolean isLoggedIn = false;
    
    @BeforeMethod
    @Parameters("browser")
    public void setUp(@Optional("chrome") String browser, ITestContext context) {
        logger.info("\n\n------------New Test Started--------------");
        logger.info("Browser: " + browser);
        
        // Initialize configuration
        config = ConfigManager.getInstance();
        
        // Initialize driver using DriverFactory
        driver = DriverFactory.initDriver(browser);
        
        // Store test info in context for reporting
        context.setAttribute("browser", browser);
        
        logger.info("Driver initialized successfully");
    }
    
    @AfterMethod
    public void tearDown(ITestContext context) {
        // Quit driver using DriverFactory
        DriverFactory.quitDriver();
        logger.info("Driver quit successfully");
        logger.info("------------Test Ended--------------\n");
    }
    
    /**
     * Navigate to the base URL
     */
    protected void navigateToBaseUrl() {
        String baseUrl = config.getBaseUrl();
        driver.get(baseUrl);
        logger.info("Navigated to: " + baseUrl);
    }
    
    /**
     * Navigate to a specific URL
     */
    protected void navigateTo(String url) {
        driver.get(url);
        logger.info("Navigated to: " + url);
    }
    
    /**
     * Perform login with phone number and manual OTP entry.
     * @param waitSeconds Seconds to wait for manual OTP entry
     * @return true if login was successful
     */
    protected boolean performLogin(int waitSeconds) throws InterruptedException {
        logger.info("Performing login with phone number...");
        
        String loginURL = config.getLoginUrl();
        driver.get(loginURL);
        FlipkartLoginPage loginPage = new FlipkartLoginPage(driver);
        
        String phoneNo = config.getPhoneNumber();
        logger.info("Using phone number: " + phoneNo);
        
        loginPage.clickPhoneInputBox();
        loginPage.enterPhoneNumber(phoneNo);
        loginPage.clickRequestOTP();
        
        // Wait for user to manually enter OTP
        loginPage.waitForManualOTPEntry(waitSeconds);
        
        // Check if login was successful
        isLoggedIn = loginPage.isLoginSuccessful();
        if (isLoggedIn) {
            logger.info("Login successful!");
        } else {
            logger.warn("Login may not have completed successfully");
        }
        return isLoggedIn;
    }
    
    /**
     * Perform login with default 15 seconds wait time
     */
    protected boolean performLogin() throws InterruptedException {
        return performLogin(15);
    }
    
    /**
     * Get the current page title
     */
    protected String getPageTitle() {
        return driver.getTitle();
    }
    
    /**
     * Get the current URL
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
