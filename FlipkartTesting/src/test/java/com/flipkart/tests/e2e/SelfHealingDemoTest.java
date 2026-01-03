package com.flipkart.tests.e2e;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.flipkart.pages.FlipkartSelfHealingDemo;
import com.flipkart.tests.base.BaseTest;

/**
 * Self-Healing Demo Test
 * 
 * This test demonstrates the AI-powered self-healing feature.
 * To see healing in action:
 * 
 * 1. Break the searchButton selector in flipkart-homepage.locators.json
 * 2. Start the healing server: npm run server
 * 3. Run this test
 * 4. Watch the console for healing logs
 */
public class SelfHealingDemoTest extends BaseTest {

    @Test(description = "Demo: Watch AI heal a broken search button selector")
    public void testSearchButtonHealing() {
        logger.info("=== SELF-HEALING DEMO TEST ===");
        logger.info("This test will demonstrate AI-powered locator healing");

        // Navigate to Flipkart
        navigateToBaseUrl();
        logger.info("Navigated to Flipkart homepage");

        // Create self-healing page object
        FlipkartSelfHealingDemo page = new FlipkartSelfHealingDemo(driver);

        // Verify we're on the right page
        Assert.assertTrue(page.isSearchBoxVisible(), "Search box should be visible");
        logger.info("✅ Search box is visible");

        // This will trigger self-healing if searchButton selector is broken:
        // 1. Primary selector fails
        // 2. Fallback selectors fail
        // 3. Healing engine captures DOM
        // 4. LLM generates new selectors
        // 5. Framework validates and uses healed selector
        // 6. Search executes successfully
        logger.info("🔧 Attempting search with potentially broken selector...");
        page.searchForProduct("iPhone 15 Pro");

        logger.info("✅ Search executed successfully!");
        logger.info("If the selector was broken, check the logs above for healing events");
        logger.info("=== DEMO COMPLETE ===");
    }

    @Test(description = "Simple test without healing - baseline comparison")
    public void testSearchWithoutHealing() {
        logger.info("=== BASELINE TEST (No Healing Expected) ===");

        navigateToBaseUrl();
        FlipkartSelfHealingDemo page = new FlipkartSelfHealingDemo(driver);

        // With correct selectors, this should work without healing
        page.searchForProduct("Samsung Galaxy");

        logger.info("✅ Baseline test complete");
    }
}
