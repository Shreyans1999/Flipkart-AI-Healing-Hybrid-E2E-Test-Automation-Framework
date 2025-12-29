package com.flipkart.tests.e2e;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.flipkart.pages.FlipkartChangePincode;
import com.flipkart.utils.WindowUtils;
import com.flipkart.tests.base.BaseTest;

/**
 * Location/Pincode related tests
 */
public class LocationTests extends BaseTest {

    @Test(groups = {"e2e", "location"})
    public void changingPinCode() throws InterruptedException {
        logger.info("Changing the pincode");
        
        // Navigate to a product page first
        String productLink = config.getProductLink();
        if (productLink != null) {
            navigateTo(productLink);
        } else {
            navigateToBaseUrl();
        }
        Thread.sleep(2000);
        
        // Handle new tab if opened
        String originalTab = WindowUtils.switchToNewWindow(driver);
        
        // Perform actions on the new tab
        FlipkartChangePincode pincodePage = new FlipkartChangePincode(driver);
        String pincode = config.getPincode();
        pincodePage.enterPincode(pincode);
        
        // Assertion
        String expectedText = "Delivery";
        String actualText = pincodePage.getExpectedText();
        Assert.assertTrue(actualText.contains(expectedText), 
            "Expected text not found: " + expectedText);
        
        // Switch back to previous tab if needed
        if (originalTab != null) {
            driver.switchTo().window(originalTab);
        }
        
        logger.info("Pincode changed successfully to: " + pincode);
    }
}