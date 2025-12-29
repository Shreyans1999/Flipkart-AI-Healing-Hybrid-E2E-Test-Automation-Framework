package com.flipkart.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class FlipkartClickProductHome extends BasePage {
	
	public FlipkartClickProductHome(WebDriver driver) {
		super(driver);
	}
	
	// Using class pattern verified on live Flipkart homepage
	@FindBy(xpath="(//a[contains(@class, '_3n8fna')])[3]")
	public WebElement productImg;
	
	@FindBy(xpath="(//img[@title=\"Flipkart\"])")
	public WebElement HomeButton;
	
	public void clickOnHomeBtn() {
		HomeButton.click();
	}
	
	public boolean clickOnProduct() {
		// First, try to dismiss any popup overlays (login popup, promo banners)
		closeLoginPopupIfPresent();
		
		WebElement visibleProductImage = waitForVisible(productImg);
		scrollIntoView(visibleProductImage);
		pause(500); // Wait for scroll to complete
		
		safeClick(visibleProductImage);
		
		return true;
	}
}
