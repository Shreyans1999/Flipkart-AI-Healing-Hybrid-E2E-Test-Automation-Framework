package com.flipkart.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class FlipkartSortFilter extends BasePage {
	
	public FlipkartSortFilter(WebDriver driver) {
		super(driver);
	}

	@FindBy(xpath = "//div[text()='Price -- Low to High']")
    private WebElement lowToHigh;
	
	public boolean applySortLowToHigh() throws InterruptedException {
		pause(1000);
		lowToHigh.click();
		return true;
	}

}
