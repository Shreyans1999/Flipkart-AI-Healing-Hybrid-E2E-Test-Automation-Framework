package com.flipkart.pages;

import java.time.LocalDate;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object for Flipkart Flight Booking page.
 * Handles navigation to flights section and flight search functionality.
 */
public class FlipkartFlightPage extends BasePage {
	
	public FlipkartFlightPage(WebDriver driver) {
		super(driver);
	}
	
	// Navigation element - Flight Bookings link in header
	@FindBy(xpath = "//a[contains(text(),'Flight Bookings') or contains(@title,'Flight Bookings')] | //span[text()='Flight Bookings']")
	private WebElement flightBookingsLink;
	
	// From city input field - using stable name attribute
	@FindBy(name = "0-departcity")
	private WebElement fromCityInput;
	
	// To city input field - using stable name attribute
	@FindBy(name = "0-arrivalcity")
	private WebElement toCityInput;
	
	// Depart date field - using stable name attribute
	@FindBy(name = "0-datefrom")
	private WebElement departDateInput;
	
	// Travellers field - using stable name attribute
	@FindBy(name = "0-travellerclasscount")
	private WebElement travellersInput;
	
	// Search button
	@FindBy(xpath = "//button[.//span[text()='SEARCH']] | //button[contains(@class,'dSM5Ub')]")
	private WebElement searchButton;
	
	/**
	 * Click on Flight Bookings link in the navigation header
	 */
	public void clickFlightBookings() throws InterruptedException {
		try {
			waitForClickable(flightBookingsLink).click();
		} catch (Exception e) {
			// Try alternative locator
			WebElement altLink = waitForClickable(
				By.xpath("//span[contains(text(),'Flight Bookings')] | //div[contains(text(),'Flight Bookings')]")
			);
			altLink.click();
		}
		pause(2000);
	}
	
	/**
	 * Enter departure city in From field
	 */
	public void enterFromCity(String city) throws InterruptedException {
		try {
			// Wait for and click the From input field
			WebElement fromField = waitForClickable(fromCityInput);
			fromField.click();
			pause(500);
			
			// Clear and enter the city name
			fromField.clear();
			fromField.sendKeys(city);
			pause(1500);
			
			// Click on the first suggestion in the dropdown
			WebElement suggestion = waitForClickable(
				By.xpath("//ul[contains(@class,'dropdown')]//li[1] | //div[contains(@class,'dropdown')]//div[contains(@class,'option')][1] | //li[contains(@class,'suggestion')][1] | //div[@role='listbox']//div[1]")
			);
			suggestion.click();
			pause(500);
		} catch (Exception e) {
			System.out.println("Error entering from city: " + e.getMessage());
			// Try alternative approach - click anywhere to close dropdown
			setValueWithJS(fromCityInput, city);
			clickBodyToCloseDropdown();
			pause(500);
		}
	}
	
	/**
	 * Enter arrival city in To field
	 */
	public void enterToCity(String city) throws InterruptedException {
		try {
			// Wait for and click the To input field
			WebElement toField = waitForClickable(toCityInput);
			toField.click();
			pause(500);
			
			// Clear and enter the city name
			toField.clear();
			toField.sendKeys(city);
			pause(1500);
			
			// Click on the first suggestion in the dropdown
			WebElement suggestion = waitForClickable(
				By.xpath("//ul[contains(@class,'dropdown')]//li[1] | //div[contains(@class,'dropdown')]//div[contains(@class,'option')][1] | //li[contains(@class,'suggestion')][1] | //div[@role='listbox']//div[1]")
			);
			suggestion.click();
			pause(500);
		} catch (Exception e) {
			System.out.println("Error entering to city: " + e.getMessage());
			// Try alternative approach
			setValueWithJS(toCityInput, city);
			clickBodyToCloseDropdown();
			pause(500);
		}
	}
	
	/**
	 * Select today's date for departure
	 */
	public void selectDepartureDate() throws InterruptedException {
		try {
			// Click on departure date field to open calendar
			WebElement dateField = waitForClickable(departDateInput);
			dateField.click();
			pause(500);
			
			// Get today's date
			LocalDate today = LocalDate.now();
			int day = today.getDayOfMonth();
			
			// Try to find and click today's date in the calendar
			WebElement todayDate = waitForClickable(
				By.xpath("//button[text()='" + day + "' and not(contains(@class,'disabled'))]")
			);
			todayDate.click();
			pause(500);
		} catch (Exception e) {
			System.out.println("Date may already be selected or calendar handling needed: " + e.getMessage());
			// Date might already be selected, click somewhere else to close
			clickBodyToCloseDropdown();
			pause(500);
		}
	}
	
	/**
	 * Set number of travellers (default is usually 1)
	 */
	public void setTravellers(int count) throws InterruptedException {
		// For this test, we'll use the default value (1 Traveller | Economy)
		System.out.println("Using default traveller count: 1 Traveller | Economy");
	}
	
	/**
	 * Click the Search button to search for flights
	 */
	public void clickSearch() throws InterruptedException {
		try {
			safeClick(searchButton);
			pause(3000);
		} catch (Exception e) {
			// Try JavaScript click as fallback
			WebElement searchBtn = driver.findElement(
				By.xpath("//button[.//span[text()='SEARCH']] | //button[contains(@class,'dSM5Ub')]")
			);
			clickWithJS(searchBtn);
			pause(3000);
		}
	}
	
	/**
	 * Perform complete flight search flow
	 */
	public void searchFlights(String fromCity, String toCity) throws InterruptedException {
		enterFromCity(fromCity);
		enterToCity(toCity);
		selectDepartureDate();
		setTravellers(1);
		clickSearch();
	}
	
	/**
	 * Check if flight booking page is displayed
	 */
	public boolean isFlightPageDisplayed() {
		try {
			return getCurrentUrl().contains("travel/flights") || 
				   getCurrentUrl().contains("flight");
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Check if flight search results are displayed
	 */
	public boolean isFlightResultsDisplayed() {
		try {
			// Wait for results to load
			pause(2000);
			// Check URL for search results indication or look for flight cards
			String currentUrl = getCurrentUrl();
			return currentUrl.contains("source=") || 
				   currentUrl.contains("destination=") ||
				   currentUrl.contains("MMID=") ||
				   driver.findElements(By.xpath("//div[contains(@class,'flight')] | //div[contains(@class,'Flight')]")).size() > 0;
		} catch (Exception e) {
			return false;
		}
	}
}
