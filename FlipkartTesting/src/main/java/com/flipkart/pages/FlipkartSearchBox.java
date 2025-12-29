package com.flipkart.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class FlipkartSearchBox extends BasePage {
	
	public FlipkartSearchBox(WebDriver driver) {
		super(driver);
	}
	
	@FindBy(xpath="//input[@name=\"q\"]")
	public WebElement searchBox;
	
	@FindBy(xpath="//button[@type=\"submit\"]")
	public WebElement searchButton;
	
	@FindBy(xpath="//div[contains(text(),'Apple iPhone 17 Pro')]")
	public WebElement productLink;
	
	
	public void clickSearchBox() {
		searchBox.click();
	}
	
	public void enterProductName(String productName) {
		searchBox.sendKeys(productName);
	}
	
	public boolean clickProduct() {
		productLink.click();
		return true;
	}
	
	public void clickSearchButton() {
		searchButton.click();
	}

}
