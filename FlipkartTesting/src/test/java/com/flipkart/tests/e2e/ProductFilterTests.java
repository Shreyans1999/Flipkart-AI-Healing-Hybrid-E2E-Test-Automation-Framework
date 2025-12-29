package com.flipkart.tests.e2e;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.flipkart.pages.FlipkartBatteryFilter;
import com.flipkart.pages.FlipkartBrandFilters;
import com.flipkart.pages.FlipkartPriceFilters;
import com.flipkart.pages.FlipkartRatingFilters;
import com.flipkart.pages.FlipkartSortFilter;
import com.flipkart.tests.base.BaseTest;

/**
 * Product filter and sort test cases
 */
public class ProductFilterTests extends BaseTest {

    @Test(groups = {"e2e", "filter"})
    public void sortProduct() throws InterruptedException {
        logger.info("Sorting products from Low To High Prices");
        
        String filterLink = config.getFilterLink();
        navigateTo(filterLink);
        Thread.sleep(2000);
        
        FlipkartSortFilter sortFilter = new FlipkartSortFilter(driver);
        boolean isClicked = sortFilter.applySortLowToHigh();
        
        // Wait to visually confirm filter was applied
        Thread.sleep(3000);
        
        Assert.assertTrue(isClicked, "Sort was not applied");
        logger.info("Sort filter applied successfully");
    }

    @Test(groups = {"e2e", "filter"})
    public void applyPriceFilter() throws InterruptedException {
        logger.info("Applying the price filter on search results");
        
        String filterLink = config.getFilterLink();
        navigateTo(filterLink);
        Thread.sleep(2000);
        
        FlipkartPriceFilters priceFilter = new FlipkartPriceFilters(driver);
        boolean isClicked = priceFilter.priceTag();
        
        // Wait to visually confirm filter was applied
        Thread.sleep(3000);
        
        Assert.assertTrue(isClicked, "Price filter was not applied");
        logger.info("Price filter applied successfully");
    }

    @Test(groups = {"e2e", "filter"})
    public void applyBrandFilter() throws InterruptedException {
        logger.info("Applying the brand filter on search results");
        
        String filterLink = config.getFilterLink();
        navigateTo(filterLink);
        Thread.sleep(2000);
        
        FlipkartBrandFilters brandFilter = new FlipkartBrandFilters(driver);
        boolean isClicked = brandFilter.brandFilter();
        
        // Wait to visually confirm filter was applied
        Thread.sleep(3000);
        
        Assert.assertTrue(isClicked, "Brand filter was not applied");
        logger.info("Brand filter applied successfully");
    }

    @Test(groups = {"e2e", "filter"})
    public void applyRatingFilter() throws InterruptedException {
        logger.info("Applying the rating filter on search results");
        
        String filterLink = config.getFilterLink();
        navigateTo(filterLink);
        Thread.sleep(2000);
        
        FlipkartRatingFilters ratingFilter = new FlipkartRatingFilters(driver);
        boolean isClicked = ratingFilter.ratingFilter();
        
        // Wait to visually confirm filter was applied
        Thread.sleep(3000);
        
        Assert.assertTrue(isClicked, "Rating filter was not applied");
        logger.info("Rating filter applied successfully");
    }

    @Test(groups = {"e2e", "filter"})
    public void applyBatteryCapacityFilter() throws InterruptedException {
        logger.info("Applying the Battery Capacity filter on search results");
        
        String filterLink = config.getFilterLink();
        navigateTo(filterLink);
        Thread.sleep(2000);
        
        FlipkartBatteryFilter batteryFilter = new FlipkartBatteryFilter(driver);
        boolean isClicked = batteryFilter.selectBatteryCapacity();
        
        // Wait to visually confirm filter was applied
        Thread.sleep(3000);
        
        Assert.assertTrue(isClicked, "Battery filter was not applied");
        logger.info("Battery filter applied successfully");
    }
}