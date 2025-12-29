package com.flipkart.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class FlipkartOfferZonePage extends BasePage {
	
	public FlipkartOfferZonePage(WebDriver driver) {
		super(driver);
	}
	
	@FindBy(xpath="//a[text()=\"Offer Zone\"]")
	private WebElement offerZone;
	
	@FindBy(xpath = "(//img[@alt=\"ADIDAS SUPERNOVA 2 W Running Shoes For Women\"])")
    private WebElement sportsWear;
	
	@FindBy(xpath="(//h1[text()=\"Sports Casual Shoes Women's Footwear\"])")
	private WebElement heading;
	
	public void clickOfferZone() throws InterruptedException {
		offerZone.click();
	}
	
	public void clickSportsWear() {
		sportsWear.click();
	}
	
	public String getHeading() {
		return heading.getText();
	}
	
}
