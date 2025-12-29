package com.flipkart.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class FlipkartBrandFilters extends BasePage {
	
	public FlipkartBrandFilters(WebDriver driver) {
		super(driver);
	}
	
	// Brand section header to scroll to first
	@FindBy(xpath = "//div[text()='BRAND' or text()='Brand']")
    private WebElement brandSection;
	
	// Google brand text div - simpler XPath that just targets the text
	@FindBy(xpath = "//div[text()='Google']")
    private WebElement googleBrand;
	
	// First product in the product list - use the product link anchor tag
	@FindBy(xpath = "(//a[@class='k7wcnx'])[1]")
    private WebElement firstProduct;

   
    public boolean brandFilter() throws InterruptedException {
    	// First scroll to the Brand section to ensure it's visible
    	pause(2000); // Wait for page to fully load
    	
    	try {
    		waitForPresence(By.xpath("//div[text()='BRAND' or text()='Brand']"));
    		scrollIntoView(brandSection);
    		pause(1000);
    	} catch (Exception e) {
    		// Brand section might already be visible, continue
    	}
    	
    	// Now find and click Google brand
    	waitForPresence(By.xpath("//div[text()='Google']"));
    	waitForClickable(googleBrand);
        
        // Scroll element to center of viewport to avoid sticky header
        scrollIntoView(googleBrand);
        pause(1000);
        
        safeClick(googleBrand);
        
        // Wait for filter to be applied and products to load
        pause(5000);
        
        // Scroll up to see products after filter is applied
        scrollToTop();
        pause(2000);
        
        // Click on the first Google phone in the list
        String productXpath = "(//a[@class='k7wcnx'])[1]";
        waitForPresence(By.xpath(productXpath));
        waitForClickable(firstProduct);
        scrollIntoView(firstProduct);
        pause(1000);
        
        safeClick(firstProduct);
        
        // Wait to see the product page
        pause(3000);
        
        return true;
     }

}
