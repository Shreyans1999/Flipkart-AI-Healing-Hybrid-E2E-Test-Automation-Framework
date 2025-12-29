package com.flipkart.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

/**
 * FlipkartRegister page - Simplified version for phone-based registration
 * Note: Currently registration flow is similar to login flow on Flipkart
 * The main checkLogin() test handles the login/registration via phone OTP
 */
public class FlipkartRegister extends BasePage {
	
	public FlipkartRegister(WebDriver driver) {
		super(driver);
	}
	
	@FindBy(xpath="//a[contains(text(),'Login')] | //span[text()='Login']")
	public WebElement loginLink;

	// Locator for phone/email input field
	@FindBy(xpath="//input[contains(@class,'c3Bd2c') and contains(@class,'yXUQVt')]")
	public WebElement phoneInputBox;
	
	@FindBy(xpath="//a[contains(text(),'New to Flipkart? Create an account')] | //a[contains(text(),'New to Flipkart')]")
	public WebElement signUpLink;
	
	@FindBy(xpath="//button[contains(@class,'dSM5Ub')] | //button[.//span[text()='CONTINUE']] | //button[contains(text(),'Continue')]")
	public WebElement submitButton;
	
	@FindBy(xpath="//input[@type='text' and contains(@class,'otp')] | //input[contains(@placeholder,'OTP')] | //input[@maxlength='6']")
	public WebElement OTP_Box;
	
	@FindBy(xpath="//button[contains(text(),'Signup') or contains(text(),'SIGNUP') or contains(text(),'Sign Up')]")
	public WebElement signupButton;
	
	public void handleActionChain() throws InterruptedException {
	    Actions actions = new Actions(driver);
	    actions.moveToElement(loginLink).perform();
	    pause(500);
	    signUpLink.click();
	}
	
	public void clickSignUpLink() {
		safeClick(signUpLink);
	}
	
	public void clickPhoneInputBox() throws InterruptedException {
		pause(2000);
		WebElement inputField = waitForPresence(
			By.xpath("//input[contains(@class,'c3Bd2c') and contains(@class,'yXUQVt')]"));
		waitForClickable(inputField).click();
	}
	
	public void enterPhoneNumber(String phoneNumber) throws InterruptedException {
		WebElement inputField = waitForPresence(
			By.xpath("//input[contains(@class,'c3Bd2c') and contains(@class,'yXUQVt')]"));
		clearAndType(inputField, phoneNumber);
	}
	
	public void clickSubmitButton() {
		safeClick(submitButton);
	}
	
	/**
	 * Wait for user to manually enter OTP received on phone
	 */
	public void waitForManualOTPEntry(int seconds) throws InterruptedException {
		System.out.println("===========================================");
		System.out.println("WAITING FOR MANUAL OTP ENTRY...");
		System.out.println("Please enter the OTP received on your phone within " + seconds + " seconds");
		System.out.println("===========================================");
		pause(seconds * 1000);
	}
	
	public boolean clickSignup() {
		try {
			safeClick(signupButton);
			return true;
		} catch (Exception e) {
			System.out.println("Signup button not found or not clickable: " + e.getMessage());
			return false;
		}
	}
}
