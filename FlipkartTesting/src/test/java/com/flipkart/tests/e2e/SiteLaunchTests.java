package com.flipkart.tests.e2e;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.flipkart.tests.base.BaseTest;

/**
 * Site launch tests - verifies Flipkart homepage loads correctly
 */
public class SiteLaunchTests extends BaseTest {

    @Test(groups = {"e2e"})
    public void verifyHomepageLoads() throws InterruptedException {
        logger.info("Launching Flipkart Site");
        
        navigateToBaseUrl();
        
        // Wait to see the homepage
        Thread.sleep(2000);
        
        // Assertion - verify URL contains flipkart
        String currentURL = getCurrentUrl();
        Assert.assertTrue(currentURL.contains("flipkart"), 
            "Not on Flipkart site. Current URL: " + currentURL);
        
        logger.info("Homepage loaded successfully: " + currentURL);
        
        // Additional wait to view the page
        Thread.sleep(2000);
    }
    
    @Test(groups = {"e2e"})
    public void verifyPageTitle() throws InterruptedException {
        logger.info("Verifying page title");
        
        navigateToBaseUrl();
        
        // Wait to see the page load
        Thread.sleep(3000);
        
        String title = getPageTitle();
        Assert.assertTrue(title.toLowerCase().contains("flipkart") || 
                         title.toLowerCase().contains("online shopping"),
            "Page title does not indicate Flipkart. Title: " + title);
        
        logger.info("Page title verified: " + title);
        
        // Additional wait to view the result
        Thread.sleep(2000);
    }
}
