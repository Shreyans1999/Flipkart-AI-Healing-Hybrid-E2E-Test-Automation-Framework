package com.flipkart.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

public class FlipkartMenApparel extends BasePage {
	
	public FlipkartMenApparel(WebDriver driver) {
		super(driver);
	}
	
	@FindBy(xpath="//span[text()=\"Fashion\"]")
	private WebElement fashion;
	
	@FindBy(xpath="//a[text()=\"Men's Top Wear\"]")
	private WebElement mensTopWear;
	
	@FindBy(xpath = "//a[text()=\"Men's T-Shirts\"]")
    private WebElement tshirts;
	
	@FindBy(xpath="//h1[contains(text(),\"Men's T Shirts\")]")
	private WebElement tshirtsHeading;
	
	public void navigateToTshirts() throws InterruptedException {
		Actions actions = new Actions(driver);
		// Hover on Fashion dropdown
        actions.moveToElement(fashion).perform();
        pause(500);
        // Hover on Men's Top Wear submenu
        actions.moveToElement(mensTopWear).perform();
        pause(500);
        // Click on Men's T-Shirts
        tshirts.click();
		pause(2000);
	}
	
	public String getHeading() {
		return tshirtsHeading.getText();
	}
   
}
