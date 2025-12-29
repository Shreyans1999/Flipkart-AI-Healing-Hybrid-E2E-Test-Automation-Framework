package com.flipkart.tests.e2e;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.flipkart.pages.FlipkartBeautyFoodPage;
import com.flipkart.pages.FlipkartFurnitureProducts;
import com.flipkart.pages.FlipkartMenApparel;
import com.flipkart.tests.base.BaseTest;

/**
 * Category browsing test cases for navigation menus.
 */
public class CategoryBrowsingTests extends BaseTest {

    @Test(groups = {"regression", "category"})
    public void testNavigateToChocolates() throws InterruptedException {
        logger.info("Testing navigation to Chocolates category");
        
        navigateToBaseUrl();
        Thread.sleep(2000);
        
        FlipkartBeautyFoodPage beautyFoodPage = new FlipkartBeautyFoodPage(driver);
        beautyFoodPage.navigateToChocolates();
        
        String heading = beautyFoodPage.getHeading();
        Assert.assertTrue(heading.toLowerCase().contains("chocolate"), 
            "Should be on Chocolates page. Heading: " + heading);
        
        logger.info("Successfully navigated to Chocolates: " + heading);
    }

    @Test(groups = {"regression", "category"})
    public void testNavigateToFurniture() throws InterruptedException {
        logger.info("Testing navigation to Blankets category");
        
        navigateToBaseUrl();
        Thread.sleep(2000);
        
        FlipkartFurnitureProducts furniturePage = new FlipkartFurnitureProducts(driver);
        furniturePage.checkBlankets();
        
        String heading = furniturePage.getHeading();
        Assert.assertTrue(heading.toLowerCase().contains("blanket"), 
            "Should be on Blankets page. Heading: " + heading);
        
        logger.info("Successfully navigated to Blankets: " + heading);
    }

    @Test(groups = {"regression", "category"})
    public void testNavigateToMensTshirts() throws InterruptedException {
        logger.info("Testing navigation to Men's T-Shirts category");
        
        navigateToBaseUrl();
        Thread.sleep(2000);
        
        FlipkartMenApparel menApparel = new FlipkartMenApparel(driver);
        menApparel.navigateToTshirts();
        
        String heading = menApparel.getHeading();
        Assert.assertTrue(heading.toLowerCase().contains("t shirt") || 
                         heading.toLowerCase().contains("tshirt"), 
            "Should be on T-Shirts page. Heading: " + heading);
        
        logger.info("Successfully navigated to T-Shirts: " + heading);
    }
}
