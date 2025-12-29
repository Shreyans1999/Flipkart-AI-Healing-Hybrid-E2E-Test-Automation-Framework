package com.flipkart.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

public class FlipkartFurnitureProducts extends BasePage {
	
	public FlipkartFurnitureProducts(WebDriver driver) {
		super(driver);
	}
	
	@FindBy(xpath="//span[text()=\"Home & Furniture\"]")
	private WebElement homeAndFurniture;
	
	@FindBy(xpath="//a[text()=\"Home Furnishings\"]")
	private WebElement homeFurnishings;
	
	@FindBy(xpath = "//a[text()=\"Blankets\"]")
    private WebElement blankets;
	
	@FindBy(xpath="//h1[contains(text(),\"Blankets\")]")
	private WebElement heading;
	
	public void checkBlankets() throws InterruptedException {
		Actions actions = new Actions(driver);
		// Hover on Home & Furniture dropdown
        actions.moveToElement(homeAndFurniture).perform();
        pause(500);
        // Hover on Home Furnishings submenu
        actions.moveToElement(homeFurnishings).perform();
        pause(500);
        // Click on Blankets
        blankets.click();
		pause(2000);
	}
	
	public String getHeading() {
		return heading.getText();
	}
	
}
