package com.flipkart.tests.regression;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.flipkart.models.AddressData;
import com.flipkart.models.UserData;
import com.flipkart.pages.FlipkartAddToCart;
import com.flipkart.pages.FlipkartPlaceOrder;
import com.flipkart.tests.base.BaseTest;

/**
 * Cart and order regression test cases.
 * REQUIRES USER LOGIN before placing an order.
 * These tests are separated from the main suite as they need manual OTP entry.
 */
public class CartAndOrderTests extends BaseTest {

    @Test(groups = {"regression", "order"})
    public void testPlaceOrderFlow() throws InterruptedException {
        logger.info("Testing place order flow - Login required");
        
        // Step 1: Perform login first (required for placing orders)
        logger.info("Step 1: Logging in...");
        boolean loginSuccess = performLogin(30); // Wait 30 seconds for manual OTP entry
        
        if (!loginSuccess) {
            logger.warn("Login may not have completed - proceeding with test anyway");
        } else {
            logger.info("Login successful - proceeding with order flow");
        }
        
        // Using POJO models for test data
        UserData userData = UserData.builder()
            .email("test@example.com")
            .phoneNumber(config.getPhoneNumber())
            .build();
        
        AddressData addressData = AddressData.builder()
            .pincode(config.getPincode())
            .addressLine1("123 Test Street")
            .city("Bangalore")
            .state("Karnataka")
            .build();
        
        logger.info("User: " + userData);
        logger.info("Address: " + addressData);
        
        // Step 2: Navigate to product page
        logger.info("Step 2: Navigating to product page...");
        String productLink = config.getProductLink();
        if (productLink != null) {
            navigateTo(productLink);
        } else {
            navigateToBaseUrl();
        }
        Thread.sleep(2000);
        
        // Step 3: Complete the order flow
        logger.info("Step 3: Completing order flow...");
        FlipkartPlaceOrder orderPage = new FlipkartPlaceOrder(driver);
        boolean result = orderPage.completePlaceOrderFlow(userData.getEmail());
        
        Assert.assertTrue(result, "Place order flow should complete successfully");
        logger.info("Place order flow test passed");
    }
    
    @Test(groups = {"regression", "order"})
    public void testAddToCartWithLogin() throws InterruptedException {
        logger.info("Testing add to cart with login");
        
        // Step 1: Perform login first
        logger.info("Step 1: Logging in...");
        boolean loginSuccess = performLogin(30);
        
        if (!loginSuccess) {
            logger.warn("Login may not have completed - proceeding with test anyway");
        }
        
        // Step 2: Navigate to product page
        logger.info("Step 2: Navigating to product page...");
        String productLink = config.getProductLink();
        if (productLink != null) {
            navigateTo(productLink);
        } else {
            navigateToBaseUrl();
        }
        Thread.sleep(2000);
        
        // Step 3: Add product to cart
        logger.info("Step 3: Adding product to cart...");
        FlipkartAddToCart cartPage = new FlipkartAddToCart(driver);
        boolean result = cartPage.clickAddToCartButton();
        
        // Wait to see the cart update
        Thread.sleep(3000);
        
        Assert.assertTrue(result, "Product should be added to cart successfully");
        logger.info("Add to cart with login test passed");
    }
}
