package com.flipkart.pages;

import org.openqa.selenium.WebDriver;

/**
 * Self-Healing Demo Page - Demonstrates AI-powered locator healing
 * 
 * This page object extends SelfHealingBasePage and uses smart* methods
 * that automatically recover from broken selectors using AI.
 */
public class FlipkartSelfHealingDemo extends SelfHealingBasePage {

    @Override
    protected String getPageName() {
        return "flipkart-homepage";
    }

    public FlipkartSelfHealingDemo(WebDriver driver) {
        super(driver);
    }

    /**
     * Search for a product - demonstrates self-healing on search button
     * If the searchButton selector is broken, AI will heal it automatically
     */
    public void searchForProduct(String query) {
        // Close login popup if present
        closeLoginPopupIfPresent();

        // Type in search box (uses smart healing)
        smartClick("searchBox");
        smartType("searchBox", query);

        // Click search button - THIS WILL TRIGGER HEALING if selector is broken
        smartClick("searchButton");
    }

    /**
     * Just click the search button - for isolated healing demo
     */
    public void clickSearchButton() {
        smartClick("searchButton");
    }

    /**
     * Check if search box is visible
     */
    public boolean isSearchBoxVisible() {
        return smartIsVisible("searchBox");
    }
}
