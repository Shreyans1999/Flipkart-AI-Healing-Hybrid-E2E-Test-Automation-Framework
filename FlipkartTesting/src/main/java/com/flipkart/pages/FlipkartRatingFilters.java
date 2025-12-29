package com.flipkart.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class FlipkartRatingFilters extends BasePage {

	public FlipkartRatingFilters(WebDriver driver) {
		super(driver);
	}
	
	@FindBy(xpath = "//div[@class='tKgS7w']//select[@class='Gn+jFg']")
    private WebElement dropdown;
	
	public boolean ratingFilter() {
		return true;
	}

}
