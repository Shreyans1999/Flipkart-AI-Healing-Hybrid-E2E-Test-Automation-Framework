package com.flipkart.tests.e2e;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.flipkart.pages.FlipkartClickProductHome;
import com.flipkart.pages.FlipkartSearchBox;
import com.flipkart.tests.base.BaseTest;

/**
 * Product search and discovery test cases
 */
public class ProductSearchTests extends BaseTest {

    @Test(groups = {"e2e", "search"})
    public void clickProductOnHomepage() throws InterruptedException {
        logger.info("Clicking on a product directly on Homepage");
        
        navigateToBaseUrl();
        
        FlipkartClickProductHome homePage = new FlipkartClickProductHome(driver);
        boolean isProductClicked = homePage.clickOnProduct();
        
        // Wait to visually confirm
        Thread.sleep(2000);
        
        Assert.assertTrue(isProductClicked, "Product was not clicked successfully");
        logger.info("Product clicked successfully");
    }

    @Test(groups = {"e2e", "search"})
    public void searchAndClickProduct() throws InterruptedException {
        logger.info("Searching a product and clicking on it");
        
        navigateToBaseUrl();
        
        FlipkartSearchBox searchPage = new FlipkartSearchBox(driver);
        String product = config.getProductName();
        
        Thread.sleep(2000);
        searchPage.enterProductName(product);
        searchPage.clickSearchButton();
        
        Thread.sleep(2000);
        boolean isProductClicked = searchPage.clickProduct();
        
        Assert.assertTrue(isProductClicked, "Product was not clicked successfully");
        logger.info("Product searched and clicked successfully");
    }
}
