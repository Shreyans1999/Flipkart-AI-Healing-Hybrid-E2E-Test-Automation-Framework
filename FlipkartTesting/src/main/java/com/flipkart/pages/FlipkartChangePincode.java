package com.flipkart.pages;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class FlipkartChangePincode extends BasePage {
	
	public FlipkartChangePincode(WebDriver driver) {
		super(driver);
	}
	
	@FindBy(xpath="//div[contains(text(),'Delivery') or contains(text(),'Usually delivered')] | //span[contains(text(),'Delivery')]")
	public WebElement expectedText;
	
	
	@FindBy(xpath="//input[@id=\"pincodeInputId\"]")
	public WebElement pincodeBox;
	
	
	public void enterPincode(String pincode) throws InterruptedException {
		// Wait to see the page load
		pause(2000);
		
		waitForClickable(pincodeBox);
        scrollIntoView(pincodeBox);
		
		// Wait to see the pincode box highlighted
		pause(1500);
		
		// Clear any existing value and enter new pincode
		clearAndType(pincodeBox, pincode);
		
		// Wait to see the entered pincode
		pause(1500);
		
		// Press Enter to submit
		pincodeBox.sendKeys(Keys.RETURN);
		
		// Wait to see the delivery result
		pause(2000);
	}
	
	public String getExpectedText() {
		return expectedText.getText();
	}

}
