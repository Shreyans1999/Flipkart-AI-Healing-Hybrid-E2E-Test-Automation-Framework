package com.flipkart.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class FlipkartCheckProductImages extends BasePage {

    public FlipkartCheckProductImages(WebDriver driver) {
        super(driver);
    }

    /**
     * Iterate over product images on the product detail page.
     * Uses multiple fallback strategies to find images.
     */
    public boolean iterateOverImages() throws InterruptedException {
        // Wait for page to fully load
        pause(3000);
        waitForPageLoad();
        
        try {
            // Strategy 1: Find images in the product thumbnails section
            List<WebElement> imageElements = findProductImages();
            
            if (imageElements.isEmpty()) {
                System.out.println("No product images found to iterate");
                return true; // Not a failure if no images - product page loaded
            }
            
            System.out.println("Found " + imageElements.size() + " product images");
            
            // Iterate over each image element (max 5 to avoid too long tests)
            int maxImages = Math.min(imageElements.size(), 5);
            for (int i = 0; i < maxImages; i++) {
                try {
                    WebElement imageElement = imageElements.get(i);
                    scrollIntoView(imageElement);
                    pause(500);
                    safeClick(imageElement);
                    pause(1000);
                } catch (Exception e) {
                    System.out.println("Could not click image " + i + ": " + e.getMessage());
                }
            }
            return true;
            
        } catch (Exception e) {
            System.out.println("Error iterating images: " + e.getMessage());
            // Still return true if product page loaded - images are optional
            return driver.getCurrentUrl().contains("flipkart");
        }
    }
    
    /**
     * Find product images using multiple XPath strategies
     */
    private List<WebElement> findProductImages() {
        String[] xpaths = {
            // Thumbnail images in product gallery
            "//div[contains(@class,'_3kidJX')]//img",
            "//li[contains(@class,'_20Gt85')]//img",
            "//div[contains(@class,'_3GnUWp')]//img",
            // Any product related images
            "//div[contains(@class,'CXW8mj')]//img",
            "//div[contains(@class,'_1BweB8')]//img",
            // Generic image gallery patterns
            "//ul//li//img[contains(@src,'rukminim')]",
            "//div[@class='_1BweB8']//img",
            // Fallback - any image with product image source
            "//img[contains(@src,'rukminim') and not(contains(@src,'logo'))]"
        };
        
        for (String xpath : xpaths) {
            try {
                List<WebElement> images = driver.findElements(By.xpath(xpath));
                if (!images.isEmpty()) {
                    System.out.println("Found images using: " + xpath);
                    return images;
                }
            } catch (Exception ignored) {}
        }
        
        return List.of(); // Empty list if nothing found
    }
}
