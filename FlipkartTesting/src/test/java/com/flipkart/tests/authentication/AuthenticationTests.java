package com.flipkart.tests.authentication;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.flipkart.pages.FlipkartLoginPage;
import com.flipkart.pages.FlipkartRegister;
import com.flipkart.tests.base.BaseTest;

/**
 * Authentication test cases - Login and Registration
 */
public class AuthenticationTests extends BaseTest {

    @Test(groups = {"authentication"}, priority = 1)
    public void verifyRegisterPageElements() throws InterruptedException {
        logger.info("Verifying Register page elements are accessible");
        
        FlipkartRegister registerPage = new FlipkartRegister(driver);
        
        // Navigate to login page first
        String loginURL = config.getLoginUrl();
        driver.get(loginURL);
        Thread.sleep(2000);
        
        // Verify "New to Flipkart? Create an account" link is present and clickable
        registerPage.clickSignUpLink();
        logger.info("Sign Up link clicked successfully");
        Thread.sleep(1000);
        
        // Verify phone input box is present and accessible
        String phoneNo = config.getPhoneNumber();
        registerPage.clickPhoneInputBox();
        registerPage.enterPhoneNumber(phoneNo);
        logger.info("Phone input box is working - entered phone number");
        
        // Verify Submit/Continue button is present
        registerPage.clickSubmitButton();
        logger.info("Submit button clicked successfully");
        
        // Test passes if all elements are accessible
        Assert.assertTrue(true, "Register page elements verified");
    }

    @Test(groups = {"authentication"}, priority = 2)
    public void verifyLoginWithPhoneOTP() throws InterruptedException {
        logger.info("Checking Login Functionality using Phone Number");
        
        String loginURL = config.getLoginUrl();
        driver.get(loginURL);
        FlipkartLoginPage loginPage = new FlipkartLoginPage(driver);
        
        // Get phone number from config
        String phoneNo = config.getPhoneNumber();
        logger.info("Using phone number: " + phoneNo);
        
        // Click on phone input and enter phone number
        loginPage.clickPhoneInputBox();
        loginPage.enterPhoneNumber(phoneNo);
        
        // Click Request OTP button
        loginPage.clickRequestOTP();
        
        // Wait for user to manually enter OTP (15 seconds)
        loginPage.waitForManualOTPEntry(15);
        
        // Check if login was successful
        boolean isLoginSuccessful = loginPage.isLoginSuccessful();
        
        Assert.assertTrue(isLoginSuccessful, "Login was not successful");
        logger.info("Login completed successfully!");
    }
}
