package com.flipkart.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class FlipkartBatteryFilter extends BasePage {

    @FindBy(xpath = "//div[text()='3000 - 3999 mAh']/preceding-sibling::div")
    private WebElement batteryCapacityCheckbox;
    
    @FindBy(xpath="//div[text()='Battery Capacity']/..")
    private WebElement batteryCapacity;

    
    public FlipkartBatteryFilter(WebDriver driver) {
        super(driver);
    }

    public boolean selectBatteryCapacity() throws InterruptedException {
    	waitForClickable(batteryCapacity);
        // Scroll element to center of viewport to avoid sticky header
        scrollIntoView(batteryCapacity);
        pause(500);
        
        safeClick(batteryCapacity);
        
        waitForClickable(batteryCapacityCheckbox);
        scrollIntoView(batteryCapacityCheckbox);
        pause(500);
        
        safeClick(batteryCapacityCheckbox);
        
        return true;
    }
}
