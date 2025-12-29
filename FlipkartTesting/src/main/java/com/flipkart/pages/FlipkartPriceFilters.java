package com.flipkart.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

public class FlipkartPriceFilters extends BasePage {
	
	public FlipkartPriceFilters(WebDriver driver) {
		super(driver);
	}
	
	@FindBy(xpath = "(//select[contains(@class,'hbnjE2') or contains(@class,'Gn+jFg')])[1] | (//div[contains(text(),'Min')]/following-sibling::select)[1]")
    private WebElement dropdown;
   
	
	public boolean priceTag() {
		Select dropdownSelect = new Select(dropdown);
		dropdownSelect.selectByValue("10000");
		dropdownSelect.selectByValue("20000");
		return true;
	}
}
