package com.flipkart.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class FlipkartBeautyFoodPage extends BasePage {
	
	public FlipkartBeautyFoodPage(WebDriver driver) {
		super(driver);
	}
	
	@FindBy(xpath="//span[text()=\"Beauty, Food..\"]")
	private WebElement beautyFood;
	
	@FindBy(xpath = "//a[text()=\"Food & Drinks\"]")
    private WebElement foodAndDrinks;
	
	@FindBy(xpath="//h1[contains(text(),\"Chocolates\")]")
	private WebElement chocolatesHeading;
	
	public void navigateToChocolates() throws InterruptedException {
		Actions actions = new Actions(driver);
		
		// Hover on Beauty, Food.. dropdown
        actions.moveToElement(beautyFood).perform();
        pause(500);
        
        // Hover on Food & Drinks submenu
        actions.moveToElement(foodAndDrinks).perform();
        pause(500);
        
        // Wait for and find the Chocolates link in the dropdown menu (not footer)
        WebElement chocolatesLink = wait.until(ExpectedConditions.elementToBeClickable(
        	By.xpath("//a[text()='Chocolates' and contains(@href, 'categorytree')]")
        ));
        
        // Click using JavaScript to bypass any overlay
        clickWithJS(chocolatesLink);
		pause(2000);
	}
	
	public String getHeading() {
		return chocolatesHeading.getText();
	}
}
