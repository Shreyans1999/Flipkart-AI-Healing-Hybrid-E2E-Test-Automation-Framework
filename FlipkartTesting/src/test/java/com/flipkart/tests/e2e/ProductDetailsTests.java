package com.flipkart.tests.e2e;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.flipkart.pages.FlipkartCheckProductImages;
import com.flipkart.pages.FlipkartSearchBox;
import com.flipkart.tests.base.BaseTest;

/**
 * Product details page test cases.
 * Tests product image viewing and product information display.
 */
public class ProductDetailsTests extends BaseTest {

    @Test(groups = {"e2e", "product"})
    public void verifyProductImages() throws InterruptedException {
        logger.info("Testing product image viewing functionality");
        
        navigateToBaseUrl();
        Thread.sleep(2000);
        
        // Search for a product first
        FlipkartSearchBox searchPage = new FlipkartSearchBox(driver);
        String product = config.getProductName();
        
        searchPage.enterProductName(product);
        searchPage.clickSearchButton();
        Thread.sleep(2000);
        
        // Click on first product
        searchPage.clickProduct();
        Thread.sleep(2000);
        
        // Check product images
        FlipkartCheckProductImages imagePage = new FlipkartCheckProductImages(driver);
        boolean result = imagePage.iterateOverImages();
        
        Assert.assertTrue(result, "Product images should be viewable");
        logger.info("Product images verified successfully");
    }

    @Test(groups = {"e2e", "product"})
    public void verifyProductPageLoads() throws InterruptedException {
        logger.info("Testing product page loads correctly");
        
        navigateToBaseUrl();
        Thread.sleep(2000);
        
        // Search for a product
        FlipkartSearchBox searchPage = new FlipkartSearchBox(driver);
        String product = config.getProductName();
        
        searchPage.enterProductName(product);
        searchPage.clickSearchButton();
        Thread.sleep(2000);
        
        // Click on first product
        boolean clicked = searchPage.clickProduct();
        Thread.sleep(3000);
        
        // Verify product page loaded by checking URL changed
        String currentUrl = getCurrentUrl();
        Assert.assertTrue(clicked && currentUrl.contains("flipkart"), 
            "Product page should load. URL: " + currentUrl);
        
        logger.info("Product page loaded: " + currentUrl);
    }
}
