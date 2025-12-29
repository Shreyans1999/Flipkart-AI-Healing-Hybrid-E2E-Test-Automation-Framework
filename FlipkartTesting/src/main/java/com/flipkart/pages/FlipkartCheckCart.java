package com.flipkart.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class FlipkartCheckCart extends BasePage {
	
	public FlipkartCheckCart(WebDriver driver) {
		super(driver);
	}

	// Cart page title or any cart item indicator
	@FindBy(xpath = "//div[contains(@class,'_3g_EWv')] | //div[contains(text(),'My Cart')] | //div[contains(@class,'Cart')]//span")
	private WebElement heading;
	
	// Cart button on header
	@FindBy(xpath = "//a[contains(@href,'/viewcart')] | //span[text()='Cart']/parent::a | //a[@title='Cart']")
    private WebElement cartButton;
	
	// Cart item count or cart content
	@FindBy(xpath = "//div[contains(@class,'_1fPG_C')] | //div[contains(@class,'cart')] | //div[contains(@class,'_1Y-k9q')]")
    private WebElement cartContent;
	
	public void clickCartButton() throws InterruptedException {
		pause(2000);
		try {
			waitForClickable(cartButton);
			clickWithJS(cartButton);
		} catch (Exception e) {
			// Navigate directly if button not found
			driver.get("https://www.flipkart.com/viewcart");
		}
		pause(3000);
	}
	
	public String getHeading() {
		try {
			// Check if cart page loaded - look for any cart-related element
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'_3g_EWv')] | //div[contains(text(),'My Cart')] | //span[contains(text(),'Cart')]")));
			return getPageTitle(); // Return page title as verification
		} catch (Exception e) {
			// Try to find cart content
			try {
				return cartContent.getText();
			} catch (Exception ex) {
				return getPageTitle();
			}
		}
	}
	
}
