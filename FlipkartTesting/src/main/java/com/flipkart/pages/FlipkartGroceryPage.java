package com.flipkart.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

public class FlipkartGroceryPage extends BasePage {
	
	public FlipkartGroceryPage(WebDriver driver) {
		super(driver);
	}
	
	@FindBy(xpath="//a[text()=\"Grocery\"]")
	private WebElement Grocery;
	
	@FindBy(xpath = "//div[text()=\"Packaged Food\"]")
    private WebElement packagedFood;
	
	@FindBy(xpath="//a[text()=\"Noodles & Pasta\"]")
	private WebElement noodles_pasta;
	
	@FindBy(xpath="//a[text()=\"Vermicelli\"]")
	private WebElement vermicelli;
	
	@FindBy(xpath="//h1[text()=\"Vermicelli\"]")
	private WebElement heading;
	
	public void clickGrocery() throws InterruptedException {
		pause(1000);
		Actions actions = new Actions(driver);
        actions.moveToElement(packagedFood).perform();
        actions.moveToElement(noodles_pasta).perform();
        vermicelli.click();
		pause(2000);
	}
	
	public String getHeading() {
		return heading.getText();
	}
}
